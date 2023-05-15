package tech.tresearchgroup.palila.controller;

import io.activej.config.Config;
import io.activej.config.ConfigModule;
import io.activej.eventloop.Eventloop;
import io.activej.eventloop.inspector.ThrottlingController;
import io.activej.http.AsyncHttpServer;
import io.activej.http.AsyncServlet;
import io.activej.inject.annotation.Inject;
import io.activej.inject.annotation.Provides;
import io.activej.inject.binding.OptionalDependency;
import io.activej.inject.module.Module;
import io.activej.launcher.Launcher;
import io.activej.net.PrimaryServer;
import io.activej.service.ServiceGraphModule;
import io.activej.worker.WorkerPool;
import io.activej.worker.WorkerPoolModule;
import io.activej.worker.WorkerPools;
import io.activej.worker.annotation.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tresearchgroup.palila.model.BaseSettings;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.util.stream.Stream;

import static io.activej.config.Config.ofClassPathProperties;
import static io.activej.config.Config.ofSystemProperties;
import static io.activej.config.converter.ConfigConverters.ofInetSocketAddress;
import static io.activej.config.converter.ConfigConverters.ofInteger;
import static io.activej.inject.module.Modules.combine;
import static io.activej.launchers.initializers.Initializers.*;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.stream.Collectors.joining;
import static tech.tresearchgroup.palila.controller.SslUtils.*;

public class MultiThreadedHttpsServerLauncher extends Launcher {
    /**
     * The number of threads
     */
    public static final int WORKERS = 16;
    /**
     * Loads the server properties
     */
    public static final String PROPERTIES_FILE = "http-server.properties";
    /**
     * The port which the server runs on
     */
    public static final int mainPort = 80;
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedHttpsServerLauncher.class);
    /**
     * Where to start looking for free worker ports
     */
    public static int workerPortStart = 60842;
    /**
     * Used to store SSL information
     */
    public static KeyManager[] keyManagers;

    /**
     * Whether to run as an HTTPS server
     */
    public static boolean https = true;
    /**
     * Used to store SSL information
     */
    public static TrustManager[] trustManagers;

    /**
     * When the class is created, attempt to load the SSL key manager,
     * if it fails it will set https to false
     */
    static {
        try {
            String keyStoreLoc = "./keystore.jks";
            File file = new File(keyStoreLoc);
            String store = System.getenv("STORE");
            String key = System.getenv("KEY");
            /*
            if (!file.exists()) {
                logger.error("Failed to load a key store. Attempting to create one...");
                char[] keyChar = key.toCharArray();
                KeyStore keyStore = KeyStore.getInstance("pkcs12");
                keyStore.load(null, keyChar);
                FileOutputStream fileOutputStream = new FileOutputStream(keyStoreLoc);
                keyStore.store(fileOutputStream, keyChar);
                fileOutputStream.close();
            }*/
            if (store != null && System.getenv("KEY") != null && file.exists()) {
                keyManagers = createKeyManagers(file, store, key);
            } else {
                if (BaseSettings.debug) {
                    logger.error("Failed to setup SSL. Defaulting to HTTP");
                }
                https = false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to load the trust managers from the keystore file,
     * if it fails it will set https to false
     */
    static {
        try {
            File file = new File("./keystore.jks");
            String store = System.getenv("STORE");
            if (store != null && file.exists()) {
                trustManagers = createTrustManagers(file, store);
            } else {
                https = false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Dependency inject the primary server
     */
    @Inject
    PrimaryServer primaryServer;

    /**
     * A method that gets a free worker port. It starts with the workerPortStart
     * setting and works up until it finds a free port
     *
     * @return the number of the free port
     */
    public static synchronized int getFreePort() {
        while (++workerPortStart < 65536) {
            if (!probeBindAddress(new InetSocketAddress(workerPortStart))) continue;
            if (!probeBindAddress(new InetSocketAddress("localhost", workerPortStart))) continue;
            if (!probeBindAddress(new InetSocketAddress("127.0.0.1", workerPortStart))) continue;
            return workerPortStart;
        }
        throw new AssertionError();
    }

    /**
     * Checks whether it can bind to the address
     *
     * @param inetSocketAddress the address you wish to check
     * @return true if it can, false if it can't
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean probeBindAddress(InetSocketAddress inetSocketAddress) {
        try (ServerSocket s = new ServerSocket()) {
            s.bind(inetSocketAddress);
        } catch (BindException e) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Dependency injects the primary event loop
     *
     * @param config the configuration
     * @return the event loop
     */
    @Provides
    Eventloop primaryEventloop(Config config) {
        return Eventloop.create()
            .withInitializer(ofEventloop(config.getChild("eventloop.primary")));
    }

    /**
     * Dependency injects the worker event loop
     *
     * @param config               the configuration
     * @param throttlingController the throttle controller
     * @return the event loop
     */
    @Provides
    @Worker
    Eventloop workerEventloop(Config config, OptionalDependency<ThrottlingController> throttlingController) {
        return Eventloop.create()
            .withInitializer(ofEventloop(config.getChild("eventloop.worker")))
            .withInitializer(eventloop -> eventloop.withInspector(throttlingController.orElse(null)));
    }

    /**
     * Generates the worker pool
     *
     * @param workerPools the pools
     * @param config      the configuration
     * @return the worker pool
     */
    @Provides
    WorkerPool workerPool(WorkerPools workerPools, Config config) {
        return workerPools.createPool(config.get(ofInteger(), "workers", WORKERS));
    }

    /**
     * Dependency injects the primary server
     *
     * @param primaryEventloop the event loop
     * @param workerServers    all the workers
     * @param config           the configuration
     * @return the primary server
     * @throws Exception should something crash
     */
    @Provides
    PrimaryServer primaryServer(Eventloop primaryEventloop, WorkerPool.Instances<AsyncHttpServer> workerServers, Config config) throws Exception {
        if (https) {
            return PrimaryServer.create(primaryEventloop, workerServers.getList())
                .withSslListenPort(createSslContext("TLSv1", keyManagers, trustManagers, new SecureRandom()), newCachedThreadPool(), 443)
                .withInitializer(ofPrimaryServer(config.getChild("http")));
        } else {
            return PrimaryServer.create(primaryEventloop, workerServers.getList())
                .withListenPort(mainPort);
        }
    }

    /**
     * Creates the worker server
     *
     * @param eventloop the worker event loop
     * @param servlet   the servlet
     * @param config    the configuration
     * @return the async server
     * @throws Exception should anything crash
     */
    @Provides
    @Worker
    AsyncHttpServer workerServer(Eventloop eventloop, AsyncServlet servlet, Config config) throws Exception {
        if (https) {
            return AsyncHttpServer.create(eventloop, servlet)
                .withSslListenPort(createSslContext("TLSv1", keyManagers, trustManagers, new SecureRandom()), newCachedThreadPool(), getFreePort())
                .withInitializer(ofHttpWorker(config.getChild("http")));
        } else {
            return AsyncHttpServer.create(eventloop, servlet).withListenPort(getFreePort());
        }
    }

    /**
     * Creates the configuration
     *
     * @return the configuration
     */
    @Provides
    Config config() {
        return Config.create()
            .with("http.listenAddresses", Config.ofValue(ofInetSocketAddress(), new InetSocketAddress(workerPortStart)))
            .with("workers", "" + WORKERS)
            .overrideWith(ofClassPathProperties(PROPERTIES_FILE, true))
            .overrideWith(ofSystemProperties("config"));
    }

    /**
     * Gets the final module
     *
     * @return the final module
     */
    @Override
    protected final Module getModule() {
        return combine(
            ServiceGraphModule.create(),
            WorkerPoolModule.create(),
            ConfigModule.create()
                .withEffectiveConfigLogger(),
            getBusinessLogicModule()
        );
    }

    protected Module getBusinessLogicModule() {
        return Module.empty();
    }

    /**
     * Runs the server
     *
     * @throws Exception should something crash
     */
    @Override
    protected void run() throws Exception {
        logger.info("HTTP Server is listening on {}", Stream.concat(
                primaryServer.getListenAddresses().stream().map(address -> "http://" + ("0.0.0.0".equals(address.getHostName()) ? "localhost" : address.getHostName()) + (address.getPort() != 80 ? ":" + address.getPort() : "") + "/"),
                primaryServer.getSslListenAddresses().stream().map(address -> "https://" + ("0.0.0.0".equals(address.getHostName()) ? "localhost" : address.getHostName()) + (address.getPort() != 80 ? ":" + address.getPort() : "") + "/"))
            .collect(joining(" ")));
        awaitShutdown();
    }
}

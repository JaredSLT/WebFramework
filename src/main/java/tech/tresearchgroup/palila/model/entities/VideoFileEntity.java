package tech.tresearchgroup.palila.model.entities;

import com.google.gson.annotations.JsonAdapter;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import tech.tresearchgroup.dao.model.BasicObjectInterface;
import tech.tresearchgroup.dao.model.LockType;
import tech.tresearchgroup.palila.model.BasicFormObject;
import tech.tresearchgroup.palila.model.adapters.LongIgnoreZeroAdapter;
import tech.tresearchgroup.palila.model.enums.PlaybackQualityEnum;

import java.util.Date;

public class VideoFileEntity extends BasicFormObject implements BasicObjectInterface {

    private transient Date created;

    private transient Date updated;

    private Long id;

    private LockType lockType;
    @JsonAdapter(LongIgnoreZeroAdapter.class)
    private Long views;

    private String path;

    private PlaybackQualityEnum playbackQualityEnum;

    public VideoFileEntity() {
    }

    public VideoFileEntity(@Deserialize("created") Date created,
                           @Deserialize("updated") Date updated,
                           @Deserialize("id") Long id,
                           @Deserialize("lockType") LockType lockType,
                           @Deserialize("views") Long views,
                           @Deserialize("path") String path,
                           @Deserialize("playbackQualityEnum") PlaybackQualityEnum playbackQualityEnum) {
        this.created = created;
        this.updated = updated;
        this.id = id;
        this.lockType = lockType;
        this.views = views;
        this.path = path;
    }

    @Serialize(order = 0)
    @SerializeNullable
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Serialize(order = 1)
    @SerializeNullable
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Serialize(order = 2)
    @SerializeNullable
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Serialize(order = 3)
    @SerializeNullable
    public LockType getLockType() {
        return lockType;
    }

    @Override
    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }

    @Serialize(order = 4)
    @SerializeNullable
    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    @Serialize(order = 5)
    @SerializeNullable
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Serialize(order = 6)
    @SerializeNullable
    public PlaybackQualityEnum getPlaybackQualityEnum() {
        return playbackQualityEnum;
    }

    public void setPlaybackQualityEnum(PlaybackQualityEnum playbackQualityEnum) {
        this.playbackQualityEnum = playbackQualityEnum;
    }
}

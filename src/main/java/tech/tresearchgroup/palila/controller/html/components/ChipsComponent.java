package tech.tresearchgroup.palila.controller.html.components;

import htmlflow.HtmlFlow;
import j2html.tags.DomContent;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.KeyValuePair;
import tech.tresearchgroup.palila.model.enums.RendererEnum;

import java.util.List;

import static j2html.TagCreator.*;

public class ChipsComponent {
    public static String render(boolean editable, String title, List<KeyValuePair> value, String id, RendererEnum rendererEnum) {
        return switch (rendererEnum) {
            case J2HTML -> renderJ2HTML(editable, title, value, id).render();
            default -> renderHTMLFlow(editable, title, value, id);
        };
    }

    public static String render(boolean editable, String title, List<KeyValuePair> value, String id) {
        return render(editable, title, value, id, BaseSettings.renderer);
    }

    private static DomContent renderJ2HTML(boolean editable, String title, List<KeyValuePair> value, String id) {
        if (value != null) {
            for (KeyValuePair keyValuePair : value) {
                keyValuePair.setKey(id + "-" + keyValuePair.getKey());
            }
        }
        return html(
                iffElse(editable,
                        iffElse(value != null,
                                html(
                                        label(title).withClass("subLabel"),
                                        br(),
                                        each(value, ChipsComponent::renderIndividual)
                                ),
                                html(
                                        label(title).withClass("subLabel")
                                )
                        ),
                        iff(value != null,
                                html(
                                        label(title).withClass("subLabel"),
                                        br(),
                                        each(value, ChipsComponent::renderIndividual)
                                )
                        )
                )
        );
    }

    private static DomContent renderIndividual(KeyValuePair keyValuePair) {
        return html(
                input().withType("text").isHidden().withValue(keyValuePair.getValue()).withName(keyValuePair.getKey()),
                span(keyValuePair.getValue()).withClass("chip")
        );
    }

    private static String renderHTMLFlow(boolean editable, String title, List<KeyValuePair> value, String id) {
        StringBuilder stringBuilder = new StringBuilder();
        if (editable) {
            if (value != null) {

            } else {

            }
        } else {
            if (value != null) {
                HtmlFlow.doc(stringBuilder).div()
                        .label().attrTitle(title).attrClass("subLabel").__()
                        .br().__()
                        .
            }
        }
        return stringBuilder.toString();
    }
}

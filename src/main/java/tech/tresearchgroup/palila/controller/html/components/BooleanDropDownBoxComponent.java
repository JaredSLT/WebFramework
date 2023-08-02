package tech.tresearchgroup.palila.controller.html.components;

import htmlflow.HtmlFlow;
import j2html.tags.specialized.LabelTag;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.enums.RendererEnum;

import java.util.Objects;

import static j2html.TagCreator.*;

public class BooleanDropDownBoxComponent {
    public static String render(String title, String name, Enum selectedKey, RendererEnum rendererEnum) {
        return switch (rendererEnum) {
            case J2HTML -> renderJ2HTML(title, name, selectedKey).render();
            default -> renderHTMLFlow(title, name, selectedKey);
        };
    }

    public static String render(String title, String name, Enum selectedKey) {
        return render(title, name, selectedKey, BaseSettings.renderer);
    }

    private static LabelTag renderJ2HTML(String title, String name, Enum selectedKey) {
        return label(
            text(title),
            select(
                iffElse(Objects.equals("true", selectedKey.toString().toLowerCase()),
                    option("true").withValue("true").isSelected(),
                    option("false").withValue("false")
                )
            ).withName(name)
        );
    }

    private static String renderHTMLFlow(String title, String name, Enum selectedKey) {
        StringBuilder stringBuilder = new StringBuilder();
        if(selectedKey.toString().toLowerCase().equals("true")) {
            HtmlFlow.doc(stringBuilder).div()
                .label().text(title)
                .select()
                .option().text("true").attrValue("true").attrSelected(true).__()
                .option().text("true").attrValue("false");
        } else {
            HtmlFlow.doc(stringBuilder).div()
                .label().text(title)
                .select()
                .option().text("true").attrValue("true").__()
                .option().text("true").attrValue("false").attrSelected(true);
        }
        return stringBuilder.toString();
    }
}

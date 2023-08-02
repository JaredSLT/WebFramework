package tech.tresearchgroup.palila.controller.html.components;

import htmlflow.HtmlFlow;
import j2html.tags.specialized.LabelTag;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.enums.RendererEnum;

import static j2html.TagCreator.*;

public class CheckboxComponent {
    public static String render(String name, String text, boolean checked, RendererEnum rendererEnum) {
        return switch (rendererEnum) {
            case J2HTML -> renderJ2HTML(name, text, checked).render();
            default -> renderHTMLFlow(name, text, checked);
        };
    }

    public static String render(String name, String text, boolean checked) {
        return render(name, text, checked, BaseSettings.renderer);
    }

    public static LabelTag renderJ2HTML(String name, String text, boolean checked) {
        return label(
            text(text),
            iffElse(checked,
                input().withType("checkbox").withName(name).isChecked(),
                input().withType("checkbox").withName(name)
            )
        );
    }

    public static String renderHTMLFlow(String name, String text, boolean checked) {
        StringBuilder stringBuilder = new StringBuilder();
        if(checked) {
            HtmlFlow.doc(stringBuilder).div()
                .label().text(text)
                .input().attrType(EnumTypeInputType.CHECKBOX).attrName(name).attrChecked(true);
        } else {
            HtmlFlow.doc(stringBuilder).div()
                .label().text(text)
                .input().attrType(EnumTypeInputType.CHECKBOX).attrName(name);
        }
        return stringBuilder.toString();
    }
}

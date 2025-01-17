package tech.tresearchgroup.palila.controller.html.components;

import htmlflow.HtmlFlow;
import j2html.tags.DomContent;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.enums.RendererEnum;

import static j2html.TagCreator.*;

public class AccordionComponent {
    public static String render(String id, String text, String content, RendererEnum rendererEnum) {
        return switch (rendererEnum) {
            case J2HTML -> renderJ2HTML(id, text, content).render();
            default -> renderHTMLFlow(id, text, content);
        };
    }

    public static String render(String id, String text, String content) {
        return render(id, text, content, BaseSettings.renderer);
    }

    private static DomContent renderJ2HTML(String id, String text, String content) {
        return div(
            input().withId(id).withType("radio").withName("accordion-radio").isHidden(),
            label(
                i().withClass("fas fa-arrow-alt-circle-down"),
                text(text)
            ).withClass("accordion-header c-hand").withFor(id),
            div(
                content
            ).withClass("accordion-body")
        ).withClass("accordion");
    }

    private static String renderHTMLFlow(String id, String text, String content) {
        StringBuilder stringBuilder = new StringBuilder();
        HtmlFlow.doc(stringBuilder).div().attrClass("accordion")
            .input().attrId(id).attrType(EnumTypeInputType.RADIO).attrName("accordion-radio").attrHidden(true).__()
            .label()
            .text(i().withClass("fas fa-arrow-alt-circle-down").withText(text)).attrClass("accordion-header c-hand").attrFor(id).__()
            .div().attrClass("accordion-body").text(content).__();
        return stringBuilder.toString();
    }
}

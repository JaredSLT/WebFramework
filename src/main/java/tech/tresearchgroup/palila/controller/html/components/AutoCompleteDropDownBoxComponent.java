package tech.tresearchgroup.palila.controller.html.components;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import j2html.tags.DomContent;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import org.xmlet.htmlapifaster.Label;
import org.xmlet.htmlapifaster.Span;
import tech.tresearchgroup.palila.controller.HTMLFlowController;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.enums.RendererEnum;

import java.util.List;
import java.util.Objects;

import static j2html.TagCreator.*;

public class AutoCompleteDropDownBoxComponent {
    public static String render(boolean editable, String title, String name, String selected, List<String> values, RendererEnum rendererEnum) {
        if (selected == null) {
            selected = "";
        }
        if (BaseSettings.renderer.equals(RendererEnum.HTMLFLOW)) {
            return renderHTMLFlow(editable, title, name, selected, values);
        }
        return switch (rendererEnum) {
            case J2HTML -> renderJ2HTML(editable, title, name, selected, values).render();
            default -> renderHTMLFlow(editable, title, name, selected, values);
        };
    }

    public static String render(boolean editable, String title, String name, String selected, List<String> values) {
        return render(editable, title, name, selected, values, BaseSettings.renderer);
    }

    private static DomContent renderJ2HTML(boolean editable, String title, String name, String selected, List<String> values) {
        if (selected == null) {
            selected = "";
        }
        String finalSelected = selected;
        return iffElse(editable,
            span(
                br(),
                label(title).withClass("subLabel"),
                br(),
                input().withName(name).withList(name + "-data"),
                datalist(
                    each(values, value ->
                        iffElse(Objects.equals(value.toLowerCase(), finalSelected.toLowerCase()),
                            option(value).isSelected(),
                            option(value)
                        )
                    )
                ).withId(name + "-data")
            ),
            iff(!finalSelected.equals("") && !finalSelected.equals("null"),
                span(
                    br(),
                    label(title).withClass("subLabel"),
                    br(),
                    input().withName(name).withValue(finalSelected)
                )
            )
        );
    }

    private static String renderHTMLFlow(boolean editable, String title, String name, String selected, List<String> values) {
        StringBuilder stringBuilder = new StringBuilder();
        if(editable) {
            String list = HTMLFlowController.dataListConverter(name + "-data", values, selected, HtmlFlow.doc(stringBuilder).div());
            Label<Span<Div<HtmlPage>>> data = HtmlFlow.doc(stringBuilder).div()
                .span()
                    .br().__()
                .label().text(title).attrClass("subLabel")
                .br().__()
                .input().attrName(name).attrList(name + "-data").__()
                .text(list);
        } else {
            if(!selected.equals("") && !selected.equals("null")) {
                HtmlFlow.doc(stringBuilder).div().span()
                    .br().__()
                    .label().attrTitle(title).attrClass("subLabel").__()
                    .br().__()
                    .input().attrName(name).attrValue(selected);
            }
        }
        return stringBuilder.toString();
    }

    public static DomContent renderJ2HTML(boolean editable, String title, String value, String name, String endpoint) {
        return html(
            iffElse(editable,
                iffElse(value != null && !value.equals("") && !value.equals("null"),
                    html(
                        br(),
                        label(title).withClass("subLabel"),
                        br(),
                        input().withType("text").attr("onKeyUp", "showResults(this.value, '" + endpoint + "', '" + name + "')").withValue(value),
                        div().withId(name)
                    ),
                    html(
                        br(),
                        label(title).withClass("subLabel"),
                        br(),
                        input().withType("text").attr("onKeyUp", "showResults(this.value, '" + endpoint + "', '" + name + "')"),
                        div().withId(name)
                    )
                ),
                iff(value != null && !value.equals("") && !value.equals("null"),
                    html(
                        br(),
                        label(title).withClass("subLabel"),
                        br(),
                        input().withType("text").attr("onKeyUp", "showResults(this.value, '" + endpoint + "', '" + name + "')").withValue(value),
                        div().withId(name)
                    )
                )
            )
        );
    }

    public static String renderHTMLFlow(boolean editable, String title, String value, String name, String endpoint) {
        StringBuilder stringBuilder = new StringBuilder();
        if(editable) {
            if(value != null && !value.equals("") && !value.equals("null")) {
                HtmlFlow.doc(stringBuilder).div()
                    .br().__()
                    .label().attrTitle(title).attrClass("subLabel")
                    .br().__()
                    .input().attrType(EnumTypeInputType.TEXT).addAttr("onKeyUp", "showResults(this.value, '" + endpoint + "', '" + name + "')").attrValue(value).__()
                    .__().div().attrId(name);
            } else {
                HtmlFlow.doc(stringBuilder).div()
                    .br().__()
                    .label().attrTitle(title).attrClass("subLabel")
                    .br().__()
                    .input().attrType(EnumTypeInputType.TEXT).addAttr("onKeyUp", "showResults(this.value, '" + endpoint + "', '" + name + "')");
            }
        } else {
            if(value != null && !value.equals("") && !value.equals("null")) {
                HtmlFlow.doc(stringBuilder).div()
                    .br().__()
                    .label().attrTitle(title).attrClass("subLabel")
                    .br().__()
                    .input().attrType(EnumTypeInputType.TEXT).addAttr("onKeyUp", "showResults(this.value, '" + endpoint + "', '" + name + "')").attrValue(value).__()
                    .__().div().attrId(name);
            }
        }
        return stringBuilder.toString();
    }
}

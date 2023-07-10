package tech.tresearchgroup.palila.controller.html.components;

import j2html.tags.DomContent;
import org.jetbrains.annotations.NotNull;

import static j2html.TagCreator.*;

public class PopoverComponent {
    public static DomContent renderRight(String text) {
        return div(
            i().withClass("fas fa-question-circle"),
            div(
                label(text)
            ).withClass("popover-container")
        ).withClass("popover popover-right");
    }
}

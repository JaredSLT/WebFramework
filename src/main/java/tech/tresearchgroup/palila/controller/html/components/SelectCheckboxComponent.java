package tech.tresearchgroup.palila.controller.html.components;

import j2html.tags.DomContent;
import org.jetbrains.annotations.NotNull;

import static j2html.TagCreator.input;

public class SelectCheckboxComponent {
    public static DomContent render(String name) {
        return input().withClass("selectCheckbox").withName(name).withType("checkbox");
    }

    public static DomContent render(String extraClasses, String name) {
        return input().withClass(extraClasses + " selectCheckbox").withName(name).withType("checkbox");
    }
}

package tech.tresearchgroup.palila.controller.html.components;

import j2html.TagCreator;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class EditableTitleComponent {
    public static DomContent render(boolean editable, String title, String name) {
        return TagCreator.html(
            label().withClass("overviewLabel").withText("Title:"),
            br(),
            iffElse(editable,
                iffElse(title != null,
                    input().withType("text").withValue(title).withName(name),
                    input().withType("text").withName(name)
                ),
                label(title).withClass("overviewLabel")
            )
        );
    }
}

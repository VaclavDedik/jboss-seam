package org.jboss.seam.wiki.core.ui;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.contexts.Contexts;

import antlr.ANTLRException;

public class UIWikiFormattedText extends UIOutput {
    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.WikiFormattedText";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {

        System.out.println("############### GOT IT ");

        if (!isRendered() || getValue() == null) return;
        Reader r = new StringReader((String) getValue());
        SeamTextLexer lexer = new SeamTextLexer(r);

        // TODO: This getAttribute() stuff is not NPE safe!

        // Use the WikiTextParser to resolve links
        WikiTextParser parser =
                new WikiTextParser(lexer,
                                   getAttributes().get("linkStyleClass").toString(),
                                   getAttributes().get("brokenLinkStyleClass").toString(),
                                   getAttributes().get("attachmentLinkStyleClass").toString(),
                                   getAttributes().get("inlineLinkStyleClass").toString()
                );

        try {
            parser.startRule();
        }
        catch (ANTLRException re) {
            throw new RuntimeException(re);
        }

        context.getResponseWriter().write(parser.toString());

        // Flush persistence context after parsing/rendering - resolved and updated links need to be stored
        EntityManager em = ((EntityManager)org.jboss.seam.Component.getInstance("entityManager"));
        em.joinTransaction();
        em.flush();

        // Put attachments (wiki links...) into the event context for later rendering
        Contexts.getEventContext().set("wikiTextAttachments", parser.getAttachments());
    }


}

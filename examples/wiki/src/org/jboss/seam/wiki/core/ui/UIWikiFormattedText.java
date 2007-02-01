package org.jboss.seam.wiki.core.ui;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.wiki.core.links.WikiTextParser;

import antlr.ANTLRException;

public class UIWikiFormattedText extends UIOutput {
    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.WikiFormattedText";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {

        if (!isRendered() || getValue() == null) return;
        Reader r = new StringReader((String) getValue());
        SeamTextLexer lexer = new SeamTextLexer(r);

        // Use the WikiTextParser to resolve links
        SeamTextParser parser = new WikiTextParser(lexer, "", "");
//        SeamTextParser parser = new SeamTextParser(lexer);

        try {
            parser.startRule();
        }
        catch (ANTLRException re) {
            throw new RuntimeException(re);
        }
        context.getResponseWriter().write(parser.toString());
    }


}

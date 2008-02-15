package org.jboss.seam.wiki.core.renderer;

import java.util.Set;
import java.util.LinkedHashSet;

import antlr.RecognitionException;
import antlr.ANTLRException;
import org.jboss.seam.wiki.core.engine.WikiMacro;
import org.jboss.seam.wiki.core.engine.WikiTextParser;

public class MacroWikiTextRenderer extends NullWikiTextRenderer {

    private Set<WikiMacro> macros = new LinkedHashSet<WikiMacro>();

    public String renderMacro(WikiMacro macro) {
        macros.add(macro);
        return null;
    }

    public Set<WikiMacro> getMacros() {
        return macros;
    }

    public static MacroWikiTextRenderer renderMacros(String wikiText) {
        WikiTextParser parser = new WikiTextParser(wikiText, false, false);
        MacroWikiTextRenderer renderer = new MacroWikiTextRenderer();
        try {
            parser.setRenderer(renderer).parse();
        } catch (RecognitionException rex) {
            // Swallowing, we don't really care if there was a parse error
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }
        return renderer;
    }

}

package org.jboss.seam.wiki.core.engine;

import java.util.Set;
import java.util.LinkedHashSet;

import antlr.RecognitionException;
import antlr.ANTLRException;

public class MacroWikiTextRenderer extends NullWikiTextRenderer {

    private Set<WikiMacro> macros = new LinkedHashSet<WikiMacro>();
    private StringBuilder macrosString = new StringBuilder();

    public String renderMacro(WikiMacro macro) {
        macros.add(macro);
        macrosString.append(macro.getName()).append(" ");
        return null;
    }

    public Set<WikiMacro> getMacros() {
        return macros;
    }

    public String getMacrosString() {
        return macrosString.toString();
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

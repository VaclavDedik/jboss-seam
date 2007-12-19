package org.jboss.seam.wiki.core.engine;

import org.jboss.seam.wiki.core.model.WikiMacro;
import org.jboss.seam.Component;

import java.util.Set;
import java.util.LinkedHashSet;

import antlr.RecognitionException;
import antlr.ANTLRException;

public class MacroWikiTextRenderer extends NullWikiTextRenderer {

    private Set<WikiMacro> macros = new LinkedHashSet<WikiMacro>();
    private StringBuilder macrosString = new StringBuilder();

    public void addMacro(WikiMacro macro) {
        macros.add(macro);
        macrosString.append(macro.getName()).append(" ");
    }

    public Set<WikiMacro> getMacros() {
        return macros;
    }

    public String getMacrosString() {
        return macrosString.toString();
    }

    public static MacroWikiTextRenderer renderMacros(Long areaNumber, String wikiText) {
        WikiTextParser parser = new WikiTextParser(wikiText, false, false);
        parser.setCurrentAreaNumber(areaNumber);
        parser.setResolver((WikiLinkResolver) Component.getInstance("wikiLinkResolver"));
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

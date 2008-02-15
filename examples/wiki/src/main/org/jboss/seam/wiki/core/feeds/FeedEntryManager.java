package org.jboss.seam.wiki.core.feeds;

import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.engine.*;
import org.jboss.seam.wiki.core.renderer.DefaultWikiTextRenderer;
import org.jboss.seam.wiki.core.renderer.WikiURLRenderer;
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import antlr.ANTLRException;
import antlr.RecognitionException;

@Scope(ScopeType.APPLICATION)
public abstract class FeedEntryManager<M, FE extends FeedEntry> {

    @Logger
    static Log log;

    @In
    protected WikiURLRenderer wikiURLRenderer;

    public abstract FE createFeedEntry(M source);
    public abstract void updateFeedEntry(FE feedEntry, M source);
    public abstract String getFeedEntryTitle(M source);

    protected String renderWikiText(Long currentAreaNumber, String wikiText) {
        WikiTextParser parser = new WikiTextParser(wikiText, true, true);

        parser.setCurrentAreaNumber(currentAreaNumber);
        parser.setResolver((WikiLinkResolver) Component.getInstance("wikiLinkResolver"));

        class FeedRenderer extends DefaultWikiTextRenderer {
            public String renderInternalLink(WikiLink internalLink) {
                return !internalLink.isBroken() ?
                        "<a href=\""
                        + wikiURLRenderer.renderURL(internalLink.getFile())
                        + "\">"
                        + internalLink.getDescription()
                        + "</a>" : "[Broken Link]";
            }

            // Preserve the macro that marks the end of the teaser
            public String renderMacro(WikiMacro macro) {
                if (macro.getName().equals(FeedEntry.END_TEASER_MACRO)) {
                    return FeedEntry.END_TEASER_MARKER;
                } else {
                    return "";
                }
            }
        }
        parser.setRenderer( new FeedRenderer() );

        // Run the parser
        try {
            parser.parse();

        } catch (RecognitionException rex) {
            // Swallow and log and low debug level
            log.debug( "Ignored parse error generating feed entry text: " + FormattedTextValidator.getErrorMessage(wikiText, rex) );
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }
        return parser.toString();
    }


}

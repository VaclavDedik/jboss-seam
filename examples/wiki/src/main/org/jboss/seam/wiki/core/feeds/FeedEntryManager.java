package org.jboss.seam.wiki.core.feeds;

import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.engine.DefaultWikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import antlr.ANTLRException;
import antlr.RecognitionException;

@Scope(ScopeType.APPLICATION)
public abstract class FeedEntryManager<M, FE extends FeedEntry> {

    @Logger
    static Log log;

    public abstract FE createFeedEntry(M source);
    public abstract void updateFeedEntry(FE feedEntry, M source);
    public abstract String getFeedEntryTitle(M source);

    protected String renderWikiText(Long currentAreaNumber, String wikiText) {
        WikiTextParser parser = new WikiTextParser(wikiText, true, true);

        parser.setCurrentAreaNumber(currentAreaNumber);
        parser.setResolver((WikiLinkResolver) Component.getInstance("wikiLinkResolver"));

        class FeedRenderer extends DefaultWikiTextRenderer {
            public String renderInlineLink(WikiLink inlineLink) {
                return !inlineLink.isBroken() ?
                        "<a href=\""
                        + WikiUtil.renderURL(inlineLink.getFile())
                        + "\">"
                        + inlineLink.getDescription()
                        + "</a>" : "[Broken Link]";
            }

            // Preserve the macro that marks the end of the teaser
            public String renderMacro(String macroName) {
                if (macroName.equals(FeedEntry.END_TEASER_MACRO)) {
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

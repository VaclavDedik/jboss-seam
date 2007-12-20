/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import antlr.ANTLRException;
import antlr.RecognitionException;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.util.JSF;
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.wiki.core.engine.DefaultWikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.model.WikiUploadImage;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses WikiTextParser and WikiLinkResolver to render Seam Text markup with wiki links.
 *
 * Any lexer/parser error results in WARN level log message, you can disable this in your logging
 * configuration by raising the log level for this class to ERROR.
 *
 * @author Christian Bauer
 */
public class UIWikiFormattedText extends UIOutput {

    Log log = Logging.getLog(UIWikiFormattedText.class);

    public static final String ATTR_LINK_STYLE_CLASS                = "linkStyleClass";
    public static final String ATTR_BROKEN_LINK_STYLE_CLASS         = "brokenLinkStyleClass";
    public static final String ATTR_ATTACHMENT_LINK_STYLE_CLASS     = "attachmentLinkStyleClass";
    public static final String ATTR_THUMBNAIL_LINK_STYLE_CLASS      = "thumbnailLinkStyleClass";
    public static final String ATTR_INTERNAL_TARGET_FRAME           = "internalTargetFrame";
    public static final String ATTR_EXTERNAL_TARGET_FRAME           = "externalTargetFrame";
    public static final String ATTR_LINK_BASE_FILE                  = "linkBaseFile";
    public static final String ATTR_CURRENT_AREA_NUMBER             = "currentAreaNumber";
    public static final String ATTR_ENABLE_PLUGINS                  = "enablePlugins";

    private List<String> plugins;

    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";

    public static final String COMPONENT_TYPE = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";

    public UIWikiFormattedText() {
        super();
        plugins = new ArrayList<String>();
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public String getRendererType() {
        return null;
    }

    @Override
    public void encodeBegin(FacesContext facesContext) throws IOException {
        if (!isRendered() || getValue() == null) return;

        // Use the WikiTextParser to resolve macros
        WikiTextParser parser = new WikiTextParser((String) getValue(), false, true);

        // Resolve the base document and directory we are resolving against
        final WikiFile baseFile = (WikiFile)getAttributes().get(ATTR_LINK_BASE_FILE);
        final Long currentAreaNumber = (Long)getAttributes().get(ATTR_CURRENT_AREA_NUMBER);
        parser.setCurrentAreaNumber(currentAreaNumber);

        parser.setResolver((WikiLinkResolver)Component.getInstance("wikiLinkResolver"));

        // Set a customized renderer for parser macro callbacks
        class WikiFormattedTextRenderer extends DefaultWikiTextRenderer {

            public String renderInlineLink(WikiLink inlineLink) {
                return "<a href=\""
                        + (
                            inlineLink.isBroken()
                                ? inlineLink.getUrl()
                                : WikiUtil.renderURL(inlineLink.getFile())
                           )
                        + "\" target=\""
                        + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                        + "\" class=\""
                        + (inlineLink.isBroken() ? getAttributes().get(ATTR_BROKEN_LINK_STYLE_CLASS)
                        : getAttributes().get(ATTR_LINK_STYLE_CLASS)) + "\">"
                        + inlineLink.getDescription() + "</a>";
            }

            public String renderExternalLink(WikiLink externalLink) {
                return "<a href=\""
                        + WikiUtil.escapeEmailURL(externalLink.getUrl())
                        + "\" target=\""
                        + (getAttributes().get(ATTR_EXTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_EXTERNAL_TARGET_FRAME) : "")
                        + "\" class=\""
                        + (externalLink.isBroken() ? getAttributes().get(ATTR_BROKEN_LINK_STYLE_CLASS)
                        : getAttributes().get(ATTR_LINK_STYLE_CLASS)) + "\">"
                        + WikiUtil.escapeEmailURL(externalLink.getDescription()) + "</a>";
            }

            public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) {
                return "<a href=\""
                        + WikiUtil.renderURL(baseFile)
                        + "#attachment" + attachmentNumber
                        + "\" target=\""
                        + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                        + "\" class=\""
                        + getAttributes().get(ATTR_ATTACHMENT_LINK_STYLE_CLASS) + "\">"
                        + attachmentLink.getDescription() + "[" + attachmentNumber + "]" + "</a>";
            }

            public String renderThumbnailImageInlineLink(WikiLink inlineLink) {

                // TODO: This is not typesafe and clean, need different rendering strategy for WikiUpload subclasses
                WikiUploadImage image = (WikiUploadImage)inlineLink.getFile();
                if (image.getThumbnail() == 'F') {
                    // Full size display, no thumbnail
                    //TODO: Make sure we really don't need this - but it messes up the comment form conversation:
                    //String imageUrl = WikiUtil.renderURL(image) + "&amp;cid=" + Conversation.instance().getId();
                    String imageUrl = WikiUtil.renderURL(image);
                    return "<img src='"+ imageUrl + "'" +
                            " width='"+ image.getSizeX()+"'" +
                            " height='"+ image.getSizeY() +"'/>";
                } else {
                    // Thumbnail with link display

                    // I have no idea why this needs HTML entities for the & symbol -
                    // Firefox complains about invalid XML if an & is in an attribute
                    // value!
                    //TODO: Make sure we really don't need this - but it messes up the comment form conversation:
                    // String thumbnailUrl = WikiUtil.renderURL(image) + "&amp;thumbnail=true&amp;cid=" + Conversation.instance().getId();
                    String thumbnailUrl = WikiUtil.renderURL(image) + "&amp;thumbnail=true";

                    return "<a href=\""
                            + (inlineLink.isBroken() ? inlineLink.getUrl() : WikiUtil.renderURL(image))
                            + "\" target=\""
                            + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                            + "\" class=\""
                            + getAttributes().get(ATTR_THUMBNAIL_LINK_STYLE_CLASS) + "\"><img src=\""
                            + thumbnailUrl + "\"/></a>";
                }
            }

            public String renderMacro(String macroName) {
                if (macroName == null || macroName.length() == 0) return "";
                try {
                    new Integer(macroName);
                } catch (NumberFormatException ex) {
                    // This is the name of a not-found plugin, otherwise
                    // we'd have a numeric client identifier of the
                    // included plugin component
                    return "";
                }
                UIComponent child = findComponent(plugins.get(new Integer(macroName)));
                ResponseWriter originalResponseWriter = getFacesContext().getResponseWriter();
                StringWriter stringWriter = new StringWriter();
                ResponseWriter tempResponseWriter = originalResponseWriter
                        .cloneWithWriter(stringWriter);
                getFacesContext().setResponseWriter(tempResponseWriter);
                try {
                    JSF.renderChild(getFacesContext(), child);
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                finally {
                    getFacesContext().setResponseWriter(originalResponseWriter);
                }
                return stringWriter.getBuffer().toString();
            }

            public void setAttachmentLinks(List<WikiLink> attachmentLinks) {
                // Put attachments (wiki links...) into the event context for later rendering
                setLinks("wikiTextAttachments", attachmentLinks);
            }

            public void setExternalLinks(List<WikiLink> externalLinks) {
                // Put external links (to targets not on this wiki) into the event context for later rendering
                setLinks("wikiTextExternalLinks", externalLinks);
            }

            private void setLinks(String contextVariable, List<WikiLink> links) {
                // TODO: Need some tricks here with link identifiers and attachment numbers, right now we just skip this if it's already set
                /// ... hoping that the first caller was the document renderer and not the comment renderer - that means comment attachments are broken
                List<WikiLink> contextLinks = (List<WikiLink>)Contexts.getEventContext().get(contextVariable);
                if (contextLinks == null || contextLinks.size()==0) {
                    Contexts.getEventContext().set(contextVariable, links);
                }
                        /*
                Map<Integer, WikiLink> contextLinks =
                    (Map<Integer,WikiLink>)Contexts.getEventContext().get(contextVariable);
                if (contextLinks == null) {
                    contextLinks = new HashMap<Integer, WikiLink>();
                }
                for (WikiLink link : links) {
                    contextLinks.put(link.getIdentifier(), link);
                }
                Contexts.getEventContext().set(contextVariable, contextLinks);
                */
            }
        }

        parser.setRenderer(new WikiFormattedTextRenderer());

        try {
            parser.parse();

        } catch (RecognitionException rex) {
            // Log a nice message for any lexer/parser errors, users can disable this if they want to
            log.warn( FormattedTextValidator.getErrorMessage((String) getValue(), rex) );
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }

        facesContext.getResponseWriter().write(parser.toString());

    }

    protected String addPlugin(String clientId) {
        plugins.add(clientId);
        return (plugins.size() - 1) + "";
    }

}

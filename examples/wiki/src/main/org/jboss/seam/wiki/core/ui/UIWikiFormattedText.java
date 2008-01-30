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
import org.jboss.seam.core.Events;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.util.JSF;
import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.wiki.core.engine.*;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.model.WikiUploadImage;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final String CURRENT_MACRO_EVENT_VARIABLE         = "currentMacro";
    public static final String CURRENT_MACRO_EVENT_VARIABLE_SET     = "Macro.render.";

    public static final String ATTR_LINK_STYLE_CLASS                = "linkStyleClass";
    public static final String ATTR_BROKEN_LINK_STYLE_CLASS         = "brokenLinkStyleClass";
    public static final String ATTR_ATTACHMENT_LINK_STYLE_CLASS     = "attachmentLinkStyleClass";
    public static final String ATTR_THUMBNAIL_LINK_STYLE_CLASS      = "thumbnailLinkStyleClass";
    public static final String ATTR_INTERNAL_TARGET_FRAME           = "internalTargetFrame";
    public static final String ATTR_EXTERNAL_TARGET_FRAME           = "externalTargetFrame";
    public static final String ATTR_LINK_BASE_FILE                  = "linkBaseFile";
    public static final String ATTR_CURRENT_AREA_NUMBER             = "currentAreaNumber";
    public static final String ATTR_ENABLE_PLUGINS                  = "enablePlugins";

    private Map<Integer, WikiMacro> pluginMacros;

    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";

    public static final String COMPONENT_TYPE = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";

    public UIWikiFormattedText() {
        super();
        pluginMacros = new HashMap<Integer, WikiMacro>();
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
        WikiTextParser parser = new WikiTextParser((String) getValue(), true, true);

        // Resolve the base document and directory we are resolving against
        final WikiFile baseFile = (WikiFile)getAttributes().get(ATTR_LINK_BASE_FILE);
        final Long currentAreaNumber = (Long)getAttributes().get(ATTR_CURRENT_AREA_NUMBER);
        parser.setCurrentAreaNumber(currentAreaNumber);

        parser.setResolver((WikiLinkResolver)Component.getInstance("wikiLinkResolver"));

        // Set a customized renderer for parser macro callbacks
        class WikiFormattedTextRenderer extends DefaultWikiTextRenderer {

            public String renderInternalLink(WikiLink internalLink) {
                return "<a href=\""
                        + (
                            internalLink.isBroken()
                                ? internalLink.getUrl()
                                : WikiUtil.renderURL(internalLink.getFile())
                           )
                        + (
                            internalLink.getFragment() != null
                                ? "#"+internalLink.getEncodedFragment()
                                : ""
                          )
                        + "\" target=\""
                        + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                        + "\" class=\""
                        + (internalLink.isBroken() ? getAttributes().get(ATTR_BROKEN_LINK_STYLE_CLASS)
                        : getAttributes().get(ATTR_LINK_STYLE_CLASS)) + "\">"
                        + internalLink.getDescription() + "</a>";
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

            public String renderThumbnailImageLink(WikiLink link) {

                // TODO: This is not typesafe and clean, need different rendering strategy for WikiUpload subclasses
                WikiUploadImage image = (WikiUploadImage)link.getFile();
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

                    //TODO: Make sure we really don't need this - but it messes up the comment form conversation:
                    // String thumbnailUrl = WikiUtil.renderURL(image) + "&amp;thumbnail=true&amp;cid=" + Conversation.instance().getId();
                    String thumbnailUrl = WikiUtil.renderURL(image) + "?thumbnail=true";

                    return "<a href=\""
                            + (link.isBroken() ? link.getUrl() : WikiUtil.renderURL(image))
                            + "\" target=\""
                            + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                            + "\" class=\""
                            + getAttributes().get(ATTR_THUMBNAIL_LINK_STYLE_CLASS) + "\"><img src=\""
                            + thumbnailUrl + "\"/></a>";
                }
            }

            public String renderMacro(WikiMacro macro) {

                WikiMacro pluginMacro = pluginMacros.get(macro.getPosition());
                if (pluginMacro == null) {
                    log.debug("macro is not a plugin, skipping: " + macro);
                    return "";
                }

                log.debug("preparing plugin rendering for macro: " + macro);
                UIComponent child = findComponent( pluginMacros.get(macro.getPosition()).getClientId() );
                log.debug("JSF child client identifier: " + child.getClientId(getFacesContext()));
                ResponseWriter originalResponseWriter = getFacesContext().getResponseWriter();
                StringWriter stringWriter = new StringWriter();
                ResponseWriter tempResponseWriter = originalResponseWriter
                        .cloneWithWriter(stringWriter);
                getFacesContext().setResponseWriter(tempResponseWriter);

                log.debug("setting current macro in EVENT context");
                Contexts.getEventContext().set(CURRENT_MACRO_EVENT_VARIABLE, macro);
                Events.instance().raiseEvent(CURRENT_MACRO_EVENT_VARIABLE_SET+macro.getName());

                try {
                    log.debug("rendering plugin macro: " + macro);
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

            protected String getHeadlineId(Headline h, String headline) {
                // HTML id attribute has restrictions on valid values... so the easiest way is to make this a WikiLink
                return HEADLINE_ID_PREFIX+WikiUtil.convertToWikiName(headline);
                // We also need to access it correctly, see WikiLink.java and getHeadLineLink()
            }

            protected String getHeadlineLink(Headline h, String headline) {
                return "<a href=\""+WikiUtil.renderURL(baseFile)+"#"+WikiTextRenderer.HEADLINE_ID_PREFIX+WikiUtil.convertToWikiName(headline)+"\">"
                        + headline
                       +"</a>";
            }
        }

        parser.setRenderer(new WikiFormattedTextRenderer());

        try {
            log.debug(">>> rendering wiki text");
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

    protected void addPluginMacro(Integer position, WikiMacro macro) {
        pluginMacros.put(position, macro);
    }

}

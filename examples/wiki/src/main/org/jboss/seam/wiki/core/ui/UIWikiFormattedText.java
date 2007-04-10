package org.jboss.seam.wiki.core.ui;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

import javax.faces.component.UIOutput;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Resources;
import org.jboss.seam.ui.JSF;
import org.jboss.seam.wiki.core.model.File;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.engine.WikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.Component;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Conversation;

public class UIWikiFormattedText extends UIOutput {

    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";
    public static final String COMPONENT_TYPE = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";

    public UIWikiFormattedText() {
        super();
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext facesContext) throws IOException {
        // Already done by WikiTextRenderer
    }

    public void encodeBegin(FacesContext facesContext) throws IOException {
        if (!isRendered() || getValue() == null) return;

        // Use the WikiTextParser to resolve macros
        WikiTextParser parser = new WikiTextParser((String)getValue(), false);

        // Set a customized renderer for parser macro callbacks
        parser.setRenderer(
            new WikiTextRenderer() {

                public String renderInlineLink(WikiLink inlineLink) {
                    return "<a href=\""
                            + (inlineLink.isBroken() ? inlineLink.getUrl() : WikiUtil.renderURL(inlineLink.getNode()))
                            + "\" class=\""
                            + (inlineLink.isBroken()
                                ? getAttributes().get("brokenLinkStyleClass")
                                : getAttributes().get("linkStyleClass"))
                            + "\">"
                            + inlineLink.getDescription()
                            + "</a>";
                }

                public String renderExternalLink(WikiLink externalLink) {
                    return "<a href=\""
                            + externalLink.getUrl()
                            + "\" class=\""
                            + (externalLink.isBroken()
                                ? getAttributes().get("brokenLinkStyleClass")
                                : getAttributes().get("linkStyleClass"))
                            + "\">"
                            + externalLink.getDescription()
                            + "</a>";
                }

                public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) {
                    return "<a href=\"#attachment"
                            + attachmentNumber
                            + "\" class=\""
                            + getAttributes().get("attachmentLinkStyleClass")
                            + "\">"
                            + attachmentLink.getDescription()
                            + "[" + attachmentNumber + "]"
                            + "</a>";
                }

                public String renderThumbnailImageInlineLink(WikiLink inlineLink) {
                    File file = (File)inlineLink.getNode();
                    int thumbnailWidth;
                    // TODO: We could make these sizes customizable, maybe as attributes of the JSF tag
                    switch(file.getImageMetaInfo().getThumbnail()) {
                        case 'S': thumbnailWidth = 80; break;
                        case 'M': thumbnailWidth = 160; break;
                        case 'L': thumbnailWidth = 320; break;
                        default: thumbnailWidth = file.getImageMetaInfo().getSizeX();
                    }
                    Conversation conversation = (Conversation) Component.getInstance("conversation");
                    // I have no idea why this needs HTML entities for the & symbol - Firefox complains about invalid XML if an & is in an attribute value!
                    String thumbnailUrl = WikiUtil.renderURL(inlineLink.getNode()) + "&amp;width=" + thumbnailWidth + "&amp;cid=" + conversation.getId();

                    return "<a href=\""
                            + (inlineLink.isBroken() ? inlineLink.getUrl() : WikiUtil.renderURL(inlineLink.getNode()))
                            + "\" class=\""
                            + getAttributes().get("thumbnailLinkStyleClass")
                            + "\"><img src=\""
                            + thumbnailUrl
                            + "\"/></a>";
                }

                public String renderMacro(String macroName) {
                    if (macroName == null || macroName.length() == 0) return "";

                    // Try to get the CSS for it
                    WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
                    String includeViewCSS = "/themes/" + wikiPrefs.getThemeName() + "/css/" + macroName + ".css";

                    // Prepare all the writers for rendering
                    ResponseWriter originalResponseWriter = getFacesContext().getResponseWriter();
                    StringWriter stringWriter = new StringWriter();
                    ResponseWriter tempResponseWriter = originalResponseWriter.cloneWithWriter(stringWriter);
                    getFacesContext().setResponseWriter(tempResponseWriter);

                    StringBuilder output = new StringBuilder();

                    try {
                        // Render CSS
                        InputStream is = Resources.getResourceAsStream(includeViewCSS);
                        if (is != null) {
                            output.append("<style type=\"text/css\">\n");

                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                            StringBuilder css = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                css.append(line);
                                css.append("\n");
                            }
                            is.close();

                            // Resolve any EL value binding expression present in CSS text
                            StringBuffer resolvedCSS = new StringBuffer(css.length());
                            Matcher matcher =
                                    Pattern.compile(
                                            "#" + Pattern.quote("{") + "(.*)" + Pattern.quote("}")
                                    ).matcher(css);

                            // Replace with [Link Text=>Page Name] or replace with BROKENLINK "page name"
                            while (matcher.find()) {
                                Expressions.ValueBinding valueMethod = Expressions.instance().createValueBinding("#{" + matcher.group(1) + "}");
                                String result = (String) valueMethod.getValue();
                                if (result != null) {
                                    matcher.appendReplacement(resolvedCSS, result);
                                } else {
                                    matcher.appendReplacement(resolvedCSS, "");
                                }
                            }
                            matcher.appendTail(resolvedCSS);
                            output.append(resolvedCSS);

                            output.append("</style>\n");
                        }

                        // Render the actual child component - the plugin XHTML
                        UIComponent pluginChild = findComponent(macroName);
                        if (pluginChild == null) return ""; // Swallow it
                        pluginChild.encodeBegin(getFacesContext());
                        JSF.renderChildren(getFacesContext(), pluginChild);
                        pluginChild.encodeEnd(getFacesContext());

                        output.append(stringWriter.getBuffer().toString());

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    } finally {
                        getFacesContext().setResponseWriter(originalResponseWriter);
                    }
                    return output.toString();
                }

                public void setAttachmentLinks(List<WikiLink> attachmentLinks) {
                    // Put attachments (wiki links...) into the event context for later rendering
                    Contexts.getEventContext().set("wikiTextAttachments", attachmentLinks);
                }

                public void setExternalLinks(List<WikiLink> externalLinks) {
                    // Put external links (to targets not on this wiki) into the event context for later rendering
                    Contexts.getEventContext().set("wikiTextExternalLinks", externalLinks);
                }
            }
        );

        // Run the parser
        parser.parse(true);

        facesContext.getResponseWriter().write( parser.toString() );

    }
    
}

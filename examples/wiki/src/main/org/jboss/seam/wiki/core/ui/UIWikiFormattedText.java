package org.jboss.seam.wiki.core.ui;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Resources;
import org.jboss.seam.ui.JSF;
import org.jboss.seam.wiki.core.model.GlobalPreferences;
import org.jboss.seam.Component;
import org.jboss.seam.core.Expressions;

import antlr.ANTLRException;
import com.sun.facelets.Facelet;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.compiler.SAXCompiler;

public class UIWikiFormattedText extends UIOutput {

    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.WikiFormattedText";

    private Set<String> includedViews = new HashSet<String>();

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean getRendersChildren() {
       return true;
    }

    @Override
    public void encodeBegin(FacesContext facesContext) throws IOException {

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
                                   getAttributes().get("inlineLinkStyleClass").toString(),
                                   this
                );

        try {
            parser.startRule();
        }
        catch (ANTLRException re) {
            // TODO: Do we ever get this exception?
            throw new RuntimeException(re);
        }

        facesContext.getResponseWriter().write(parser.toString());

        // Put attachments (wiki links...) into the event context for later rendering
        Contexts.getEventContext().set("wikiTextAttachments", parser.getAttachments());
    }

    String renderMacro(String macroName) {
        if (macroName == null || macroName.length() == 0) return "";

        String includeView = "/plugins/" + macroName + "/plugin.xhtml";

        // TODO: Can only include once (otherwise we'd have to renumber child identifiers recursively...)
        if (includedViews.contains(includeView)) return "[Can't use the same plugin twice!]";

        // View can't include itself
        FacesContext facesContext = getFacesContext();
        if (facesContext.getViewRoot().getViewId().equals(includeView)) return "";

        // Try to get the XHTML document
        URL url = Resources.getResource(includeView);
        if (url == null) return "";

        // Try to get the CSS for it
        GlobalPreferences globalPrefs = (GlobalPreferences) Component.getInstance("globalPrefs");
        String includeViewCSS = "/themes/" + globalPrefs.getThemeName() + "/css/" + macroName + ".css";

        // Prepare all the writers for rendering
        ResponseWriter originalResponseWriter = facesContext.getResponseWriter();
        StringWriter stringWriter = new StringWriter();
        ResponseWriter tempResponseWriter = originalResponseWriter.cloneWithWriter(stringWriter);
        facesContext.setResponseWriter(tempResponseWriter);

        StringBuilder output = new StringBuilder();

        try {
            // Render CSS
            InputStream is = Resources.getResourceAsStream(includeViewCSS);
            if (is != null) {
                output.append("<style type=\"text/css\">\n");

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder css = new StringBuilder();
                String line;
                while ( (line = reader.readLine()) != null) {
                    css.append(line);
                    css.append("\n");
                }
                is.close();

                // Resolve any EL value binding expression present in CSS text
                StringBuffer resolvedCSS = new StringBuffer(css.length());
                Matcher matcher =
                    Pattern.compile(
                        "#" +Pattern.quote("{") + "(.*)" + Pattern.quote("}")
                    ).matcher(css);

                // Replace with [Link Text=>Page Name] or replace with BROKENLINK "page name"
                while (matcher.find()) {
                    Expressions.ValueBinding valueMethod = Expressions.instance().createValueBinding("#{"+matcher.group(1)+"}");
                    String result = (String)valueMethod.getValue();
                    matcher.appendReplacement(resolvedCSS, result);
                }
                matcher.appendTail(resolvedCSS);
                output.append(resolvedCSS);

                output.append("</style>\n");
            }

            // Render XHTML
            Facelet f = new DefaultFaceletFactory(new SAXCompiler(), new DefaultResourceResolver()).getFacelet(url);

            // TODO: I'm not sure this is good...
            List storedChildren = new ArrayList(getChildren());
            getChildren().clear();

            // TODO: This is why I copy the list back and forth: apply() hammers the children
            f.apply(facesContext, this);
            JSF.renderChildren(facesContext, this);

            // TODO: And back... it's definitely in the wrong order in the component tree but the ids look ok to me...
            getChildren().addAll(storedChildren);

            output.append(stringWriter.getBuffer().toString());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            includedViews.add(includeView);
            facesContext.setResponseWriter(originalResponseWriter);
        }
        return output.toString();
    }

    public void encodeChildren(FacesContext facesContext) throws IOException {
        // Already done
    }
}

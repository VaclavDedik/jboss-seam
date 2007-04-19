package org.jboss.seam.wiki.core.ui;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.ui.UILoadStyle;
import org.jboss.seam.util.Resources;
import org.jboss.seam.wiki.core.action.PluginPreferenceEditor;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MetaTagHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * Creates a UIWikiText JSF component and substitutes macro names in wiki
 * text with real plugin includes/JSF components in the tree.
 *
 * @author Peter Muir
 */
public class WikiFormattedTextHandler extends MetaTagHandler {

    private static final String MARK = "org.jboss.seam.wiki.core.ui.WikiFormattedTextHandler";

    public static final String REGEX_MACRO =
            Pattern.quote("[") + "<=([a-zA-Z0-9]+)" + Pattern.quote("]");

    private TagAttribute valueAttribute;

    private Set<String> includedMacros;

    public WikiFormattedTextHandler(TagConfig config) {
        super(config);
        this.valueAttribute = this.getRequiredAttribute("value");
    }

    /*
    * Main apply method called by facelets to create this component.
    */
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, ELException {
        includedMacros = new HashSet<String>();
        String id = ctx.generateUniqueId(this.tagId);
        UIComponent cmp = findChildByTagId(parent, id);
        if (cmp == null) {
            cmp = createComponent(ctx);
            cmp.getAttributes().put(MARK, id);

        }
        this.nextHandler.apply(ctx, cmp);
        parent.getChildren().add(cmp);
        createPlugins(ctx, cmp);
    }

    private UIComponent createComponent(FaceletContext ctx) {
        UIWikiFormattedText wikiFormattedText = new UIWikiFormattedText();
        setAttributes(ctx, wikiFormattedText);
        return wikiFormattedText;
    }

    /*
    * Have to manually wire the component as the Facelets magic wirer
    * is a package scoped class
    */
    @Override
    protected void setAttributes(FaceletContext ctx, Object instance) {
        UIComponent cmp = (UIComponent) instance;
        setAttribute(ctx, cmp, "linkStyleClass");
        setAttribute(ctx, cmp, "brokenLinkStyleClass");
        setAttribute(ctx, cmp, "attachmentLinkStyleClass");
        setAttribute(ctx, cmp, "thumbnailLinkStyleClass");
    }

    private void setAttribute(FaceletContext ctx, UIComponent cmp, String name) {
        TagAttribute attribute = this.getAttribute(name);
        cmp.getAttributes().put(name, attribute.getObject(ctx));
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignoreAll();
    }

    /**
     * We create the plugins as first-class components here.
     * <p/>
     * The plugins need to be rendered in the correct place in the
     * rendered wikitext.  The plugin name is replaced with a placeholder.
     * <p/>
     * To allow multiple use of the same plugin
     * we replace the plugin name with a reference to the component.
     * The clientId of the component is used as the reference.  To
     * prevent parse errors in the wikitext the clientId is stored in
     * a list on the parent UIWikiFormattedText component and it's
     * position is used as the placeholder.
     * @param ctx FaceletContext
     * @param parent Parent component
     */
    private void createPlugins(FaceletContext ctx, UIComponent parent) {
        if (!(parent instanceof UIWikiFormattedText)) return;
        UIWikiFormattedText wikiFormattedText = (UIWikiFormattedText) parent;

        String unparsed = valueAttribute.getValue(ctx);
        Matcher matcher = Pattern.compile(REGEX_MACRO).matcher(unparsed);
        StringBuffer parsed = new StringBuffer();
        while (matcher.find()) {

            // Include the plugin
            String macroName = matcher.group(1);

            URL faceletURL = getPluginURL(macroName, ctx);
            if (faceletURL != null) {
                includePluginCSS(macroName, parent);
                includePluginFacelet(faceletURL, ctx, parent);
                createPreferencesEditor(macroName);
                includedMacros.add(macroName);

                // Get the placeholder to use
                String placeHolder;
                Object nextPlugin = parent.getAttributes().get(UIPlugin.NEXT_PLUGIN);
                if (nextPlugin != null) {
                    placeHolder = wikiFormattedText.addPlugin(nextPlugin.toString());
                    parent.getAttributes().remove(UIPlugin.NEXT_PLUGIN);
                } else {
                    // Best guess based plugin renderer
                    // TODO: OOBE in document live preview when typing incomplete macro names
                    placeHolder = wikiFormattedText.addPlugin(
                        (parent.getChildren().get(parent.getChildCount() - 1)
                            .getClientId( ctx.getFacesContext() )
                        )
                    );
                }
                matcher.appendReplacement(parsed, " [<=" + placeHolder + "]");
            } else {
                matcher.appendReplacement(parsed, " [<=" + macroName + "]");
            }
        }
        matcher.appendTail(parsed);
        wikiFormattedText.setValue(parsed.toString());
    }

    private URL getPluginURL(String macroName, FaceletContext ctx) {
        if (macroName == null || macroName.length() == 0 || includedMacros.contains(macroName)) return null;

        String includeView = "/plugins/" + macroName + "/plugin.xhtml";

        // View can't include itself
        String currentViewId = ctx.getFacesContext().getViewRoot().getViewId();
        if (currentViewId.equals(includeView)) return null;

        // Try to get the XHTML document
        return Resources.getResource(includeView);
    }

    private void includePluginFacelet(URL faceletURL, FaceletContext ctx, UIComponent parent) {
        // Cribbed from facelets
        VariableMapper orig = ctx.getVariableMapper();
        try {
            ctx.setVariableMapper(new VariableMapperWrapper(orig));
            ctx.includeFacelet(parent, faceletURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            ctx.setVariableMapper(orig);
        }

    }

    /*
    * Add a CSS resource to the document head
    */
    private void includePluginCSS(String macroName, UIComponent cmp) {
        // Try to get the CSS for it
        WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
        String css = "/themes/" + wikiPrefs.getThemeName() + "/css/" + macroName + ".css";
        if (Resources.getResource(css) != null) {
            UILoadStyle style = new UILoadStyle();
            style.setSrc(css);
            cmp.getChildren().add(style);
            // Clear these out in the next build phase
            ComponentSupport.markForDeletion(style);
        }
    }

    private void createPreferencesEditor(String macroName) {

        // If this plugin has preferences and editing is enabled, instantiate a
        // plugin preferences editor and put it in the PAGE context
        String pluginPreferenceName = macroName + "Preferences";
        Boolean showPluginPreferences = (Boolean) Component.getInstance("showPluginPreferences");
        Object existingEditor = Contexts.getConversationContext()
                .get(pluginPreferenceName + "Editor");
        if (showPluginPreferences != null && showPluginPreferences && existingEditor == null) {
            PluginPreferenceEditor pluginPreferenceEditor = new PluginPreferenceEditor(
                    pluginPreferenceName);
            PluginPreferenceEditor.FlushObserver observer = (PluginPreferenceEditor.FlushObserver) Component
                    .getInstance("pluginPreferenceEditorFlushObserver");
            if (pluginPreferenceEditor.getPreferenceValues().size() > 0) {
                Contexts.getConversationContext().set(pluginPreferenceName + "Editor",
                        pluginPreferenceEditor);
                observer.addPluginPreferenceEditor(pluginPreferenceEditor);
            }
        } else if (showPluginPreferences == null || !showPluginPreferences) {
            Contexts.getConversationContext().set(pluginPreferenceName + "Editor", null);
        }

    }

    /*
    * Support method to find the UIWikiFormattedText component created by
    * this tag on a previous tree build
    */
    private static UIComponent findChildByTagId(UIComponent parent, String id) {
        Iterator itr = parent.getFacetsAndChildren();
        while (itr.hasNext()) {
            UIComponent c = (UIComponent) itr.next();
            String cid = (String) c.getAttributes().get(MARK);
            if (id.equals(cid)) {
                return c;
            }
        }
        return null;
    }

}
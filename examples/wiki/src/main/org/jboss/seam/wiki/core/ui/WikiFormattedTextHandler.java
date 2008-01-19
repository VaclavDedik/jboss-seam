/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import antlr.ANTLRException;
import antlr.RecognitionException;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MetaTagHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.ResourceLoader;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UILoadStyle;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.engine.NullWikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiMacro;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.preferences.Preferences;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

/**
 * Creates a UIWikiText JSF component and substitutes macro names in wiki
 * text with real plugin includes/JSF components in the tree.
 *
 * @author Peter Muir
 * @author Christian Bauer
 */
public class WikiFormattedTextHandler extends MetaTagHandler {

    Log log = Logging.getLog(WikiFormattedTextHandler.class);

    private static final String MARK = "org.jboss.seam.wiki.core.ui.WikiFormattedTextHandler";

    private TagAttribute valueAttribute;

    public WikiFormattedTextHandler(TagConfig config) {
        super(config);
        this.valueAttribute = this.getRequiredAttribute("value");
    }

    /*
    * Main apply method called by facelets to create this component.
    */
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, ELException {
        log.debug(">>> building wiki text components for child of: " + parent.getClientId(ctx.getFacesContext()));
        String id = ctx.generateUniqueId(this.tagId);
        UIComponent cmp = findChildByTagId(parent, id);
        if (cmp == null) {
            cmp = createComponent(ctx);
            cmp.getAttributes().put(MARK, id);
        }
        log.debug("::: invoking nextHandler for child id: " + cmp.getClientId(ctx.getFacesContext()) );
        this.nextHandler.apply(ctx, cmp);
        parent.getChildren().add(cmp);
        createPlugins(ctx, cmp);
        log.debug("<<< completed building wiki text components for child of: " + parent.getClientId(ctx.getFacesContext()));
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
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_BROKEN_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_ATTACHMENT_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_THUMBNAIL_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_INTERNAL_TARGET_FRAME);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_EXTERNAL_TARGET_FRAME);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_LINK_BASE_FILE);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_CURRENT_AREA_NUMBER);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_ENABLE_PLUGINS);
    }

    private void setAttribute(FaceletContext ctx, UIComponent cmp, String name) {
        TagAttribute attribute = this.getAttribute(name);
        if (attribute != null) {
            Object o = attribute.getObject(ctx);
            if (o == null) throw new IllegalArgumentException("Attribute '" + name + "' resolved to null");
            cmp.getAttributes().put(name, o);
        }
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
    private void createPlugins(final FaceletContext ctx, final UIComponent parent) {
        if (!(parent instanceof UIWikiFormattedText)) return;
        final UIWikiFormattedText wikiFormattedText = (UIWikiFormattedText) parent;

        String unparsed = valueAttribute.getValue(ctx);

        // Don't forget this, transporting the value to the handled component
        wikiFormattedText.setValue(unparsed);

        log.debug("trying to create plugin children for component");

        if (Contexts.getEventContext().get(UIWikiFormattedText.CURRENT_MACRO_EVENT_VARIABLE) != null) {
            log.debug("disabling plugin rendering, we are trying to create plugins inside a plugin - not possible!");
            return;
        }

        if (getAttribute(UIWikiFormattedText.ATTR_ENABLE_PLUGINS) == null ||
            !getAttribute(UIWikiFormattedText.ATTR_ENABLE_PLUGINS).getBoolean(ctx)) {
            log.debug("plugin rendering disabled");
            return;
        }

        log.debug("creating plugin components from wiki text macros");

        WikiTextParser parser = new WikiTextParser(unparsed, true, false);
        parser.setRenderer(
            new NullWikiTextRenderer() {
                public String renderMacro(WikiMacro macro) {
                    log.debug("found macro: " + macro);

                    URL faceletURL = getPluginURL(macro.getName(), ctx);
                    if (faceletURL == null) return null;

                    log.debug("setting current macro in EVENT context before including facelets file");
                    Contexts.getEventContext().set(UIWikiFormattedText.CURRENT_MACRO_EVENT_VARIABLE, macro);

                    includePluginCSS(macro.getName(), parent);
                    includePluginFacelet(faceletURL, ctx, parent);

                    // TODO: Need to understand this magic from Pete if we want to make sub-clientIds for plugins
                    Object nextPluginId = parent.getAttributes().get(UIPlugin.NEXT_PLUGIN);
                    if (nextPluginId != null) {
                        macro.setClientId(nextPluginId.toString());
                        wikiFormattedText.addPluginMacro(macro.getPosition(), macro);
                        parent.getAttributes().remove(UIPlugin.NEXT_PLUGIN);
                    } else {
                        // Best guess based plugin renderer
                        String pluginId =
                            parent.getChildren().get( parent.getChildCount()-1 )
                                   .getClientId( ctx.getFacesContext() );
                        macro.setClientId(pluginId);
                        wikiFormattedText.addPluginMacro(macro.getPosition(), macro);
                    }

                    log.debug("unsetting current macro in EVENT context");
                    Contexts.getEventContext().remove(UIWikiFormattedText.CURRENT_MACRO_EVENT_VARIABLE);
                    return null;
                }
            }
        );

        try {
            parser.parse();
        } catch (RecognitionException rex) {
            // Swallow parsing errors, we don't really care here...
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }
    }

    private URL getPluginURL(String macroName, FaceletContext ctx) {
        //if (macroName == null || macroName.length() == 0 || includedMacros.contains(macroName)) return null;
        if (macroName == null || macroName.length() == 0) return null;

        String includeView = "/plugins/" + macroName + "/plugin.xhtml";

        // View can't include itself
        String currentViewId = ctx.getFacesContext().getViewRoot().getViewId();
        if (currentViewId.equals(includeView)) return null;

        // Try to get the XHTML document
        return ResourceLoader.instance().getResource(includeView);
    }

    private void includePluginFacelet(URL faceletURL, FaceletContext ctx, UIComponent parent) {
        // Cribbed from facelets
        VariableMapper orig = ctx.getVariableMapper();
        try {
            log.debug("including plugin facelets file from URL: " + faceletURL);
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
        WikiPreferences wikiPrefs = (WikiPreferences) Preferences.getInstance("Wiki");
        String css = "/themes/" + wikiPrefs.getThemeName() + "/css/" + macroName + ".css";
        if (ResourceLoader.instance().getResource(css) != null) {
            log.debug("including plugin CSS file from resource: " + css);
            // TODO: For Pete to fix, UILoadStyle doesn't load the CSS anymore
            UILoadStyle style = UILoadStyle.newInstance();
            style.setSrc(css);
            cmp.getChildren().add(style);
            // Clear these out in the next build phase
            ComponentSupport.markForDeletion(style);
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
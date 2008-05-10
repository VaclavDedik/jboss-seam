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
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.faces.ResourceLoader;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.core.plugin.metamodel.module.MacroPluginModule;
import org.jboss.seam.wiki.core.renderer.NullWikiTextRenderer;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.ajax4jsf.component.html.HtmlLoadStyle;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Creates a <tt>UIWikiFormattedText</tt> JSF component and substitutes macro names in wiki
 * text with real macro components in the tree. These <tt>UIMacro</tt> components are
 * build from XHTML fragments/includes. Interacts closely with the state of the
 * <tt>UIWikiFormattedText</tt> component to split component tree creation and rendering duties.
 *
 * @author Peter Muir
 * @author Christian Bauer
 */
public class WikiFormattedTextHandler extends MetaTagHandler {

    public static final String MACRO_STACK_PAGE_VARIABLE = "macroStack";

    private Log log = Logging.getLog(WikiFormattedTextHandler.class);

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
        this.nextHandler.apply(ctx, cmp);
        parent.getChildren().add(cmp);
        createMacroComponents(ctx, cmp);
        log.debug("<<< completed building wiki text components for child of: " + parent.getClientId(ctx.getFacesContext()));
    }

    private UIComponent createComponent(FaceletContext ctx) {
        UIWikiFormattedText wikiFormattedText = new UIWikiFormattedText();
        setAttributes(ctx, wikiFormattedText);
        return wikiFormattedText;
    }

    /*
    * Have to manually wire the component as the Facelets magic wirer
    * is a package scoped class.
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
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_ENABLE_MACRO_RENDERING, false);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_ENABLE_TRANSIENT_MACROS, false);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignoreAll();
    }

    /**
     * We create the included macro template components as first-class components here.
     * <p/>
     * This routine parses the wiki text and for each encountered wiki macro, it tries to
     * include an XHTML template. If no template is found, we do nothing. If a template is
     * found, we also include its CSS into the document header, then add it to the parent
     * component, the <tt>UIWikiFormattedText</tt> we are handling. This parent component
     * keeps a map of <tt>WikiMacro</tt> instances, keyed by position in the rendered
     * wiki text.
     * </p>
     * <p>
     * Macros are never reentrant, that means a macro can not render itself. To avoid this,
     * we push a macro onto a stack before including it in the component tree, after checking if
     * it is already present on the stack. If it is already present, we log a warning and don't do
     * anything. After rendering, we pop the stack. The stack is held in the PAGE context.
     * </p>
     * @param ctx FaceletContext
     * @param parent Parent component
     */
    private void createMacroComponents(final FaceletContext ctx, final UIComponent parent) {
        if (!(parent instanceof UIWikiFormattedText)) return;
        final UIWikiFormattedText wikiFormattedTextComponent = (UIWikiFormattedText) parent;

        String unparsed = valueAttribute.getValue(ctx);

        // Don't forget this, transporting the value to the handled component, we need to render it (again) later
        wikiFormattedTextComponent.setValue(unparsed);

        if (getAttribute(UIWikiFormattedText.ATTR_ENABLE_MACRO_RENDERING) == null ||
            !getAttribute(UIWikiFormattedText.ATTR_ENABLE_MACRO_RENDERING).getBoolean(ctx)) {
            log.debug("macro rendering disabled");
            return;
        }

        // We need to parse the wiki text once (later again for rendering) to find all macros in the text
        log.debug("creating macro components from wiki text macros");
        WikiTextParser parser = new WikiTextParser(unparsed, true, false);
        parser.setRenderer(
            new NullWikiTextRenderer() {

                // A collection of all macros (whether they have templates or not) that we found in this piece of wiki text
                final Set<String> macrosFoundInWikiText = new HashSet<String>();

                @Override
                public String renderMacro(WikiTextMacro wikiTextMacro) {
                    log.debug("=== found macro in wiki text: " + wikiTextMacro);

                    // Check reentrancy
                    if (!isMacroOnPageStack(wikiTextMacro)) {
                        log.debug("adding macro to page macro stack");
                        getPageMacroStack().push(wikiTextMacro);
                    } else {
                        log.warn("macros are not reentrant, duplicate macro on page stack: " + wikiTextMacro);
                        return null;
                    }

                    // Check if the wikiTextMacro actually is registered, we don't build unknown macros
                    WikiPluginMacro pluginMacro = PluginRegistry.instance().createWikiPluginMacro(wikiTextMacro);
                    if (pluginMacro == null) {
                        log.warn("macro is not bound in plugin registry: " + wikiTextMacro);
                        getPageMacroStack().pop();
                        return null;
                    }

                    // Check if we can find the template to include for this wikiTextMacro
                    String macroIncludePath = getMacroIncludePath(pluginMacro);
                    if (macroIncludePath == null) {
                        getPageMacroStack().pop();
                        return null;
                    }

                    // Before we build the nested components, set the WikiMacro instance in the PAGE context under a
                    // unique name, so we can use a VariableMapper later and alias this as 'currentMacro'
                    String macroPageVariableName = pluginMacro.getPageVariableName();
                    log.debug("setting WikiMacro instance in PAGE context as variable named: " + macroPageVariableName);
                    Contexts.getPageContext().set(macroPageVariableName, pluginMacro);

                    // Whoever wants to do something before we finally build the XHTML template
                    log.debug("firing VIEW_BUILD macro event");
                    Events.instance().raiseEvent(pluginMacro.getCallbackEventName(WikiPluginMacro.CallbackEvent.VIEW_BUILD), pluginMacro);

                    // This is where the magic happens... the UIWikiFormattedText component should have one child after that, a UIMacro
                    includeMacroFacelet(pluginMacro, macroIncludePath, ctx, wikiFormattedTextComponent);

                    // Now get the identifier of the newly created UIMacro instance and set it for future use
                    Object macroId = wikiFormattedTextComponent.getAttributes().get(UIMacro.NEXT_MACRO);
                    if (macroId != null) {
                        pluginMacro.setClientId(macroId.toString());
                        wikiFormattedTextComponent.getAttributes().remove(UIMacro.NEXT_MACRO);
                    } else {
                        // Best guess based wikiTextMacro renderer, needed during reRendering when we don't build the child
                        // - only then is NEXT_MACRO set by the MacroComponentHandler
                        macroId =
                            wikiFormattedTextComponent.getChildren().get(
                                wikiFormattedTextComponent.getChildCount()-1
                            ).getClientId( ctx.getFacesContext() );
                        pluginMacro.setClientId(macroId.toString());
                    }

                    // Put an optional CSS include in the header of the wiki document we are rendering in.
                    // (This needs to happen after the clientId is set, as CSS resource path rendering needs to
                    // know if it occurs in a JSF request (clientId present) or not.
                    includeMacroCSS(pluginMacro, wikiFormattedTextComponent);

                    // We need to make the UIMacro child transient if we run in the wiki text editor preview. The reason
                    // is complicated: If we don't make it transient, all value expressions inside the wikiTextMacro templates that
                    // use 'currentMacro' will refer to the "old" saved ValueExpression and then of course to the "old"
                    // VariableMapper. In other words: We need to make sure that the subtree is completely fresh every
                    // time the wiki text preview is reRendered, otherwise we never get a 'currentMacro' binding updated.
                    // This also means that VariableMapper is a completely useless construct, because it is basically an
                    // alias that is evaluated just once.
                    // Note: This means we can't click on form elements of any plugin/wikiTextMacro template in the preview. This
                    // should be solved by not showing/ghosting any form elements during preview.
                    if (getAttribute(UIWikiFormattedText.ATTR_ENABLE_TRANSIENT_MACROS) != null &&
                        getAttribute(UIWikiFormattedText.ATTR_ENABLE_TRANSIENT_MACROS).getBoolean(ctx)) {
                        log.debug("setting macro to transient rendering, not storing its state between renderings: " + pluginMacro);
                        UIMacro uiMacro = (UIMacro)ComponentSupport.findChild(wikiFormattedTextComponent, macroId.toString());
                        uiMacro.setTransient(true);
                    }

                    // Finally, pop the wikiTextMacro stack of the page, then transport the finished WikiMacro instance into
                    // the UIWikiFormattedText component for rendering - we are done building the component tree at this
                    // point.
                    getPageMacroStack().pop();
                    wikiFormattedTextComponent.addMacroWithTemplate(pluginMacro);

                    // Well, we don't render anything here...
                    return null;
                }

                private String getMacroIncludePath(WikiPluginMacro pluginMacro) {

                    // Check singleton configuration
                    if (pluginMacro.getMetadata().isRenderOptionSet(MacroPluginModule.RenderOption.SINGLETON) &&
                        macrosFoundInWikiText.contains(pluginMacro.getName())) {
                        log.warn("macro is a SINGLETON, can not be used twice in the same document area: " + pluginMacro);
                        return null;
                    } else {
                        macrosFoundInWikiText.add(pluginMacro.getName());
                    }

                    // Check skin configuration
                    String currentSkin = (String)Component.getInstance("skin");
                    if (!pluginMacro.getMetadata().isAvailableForSkin(currentSkin)) {
                        log.warn("macro is not available for skin '"+currentSkin+"': " + pluginMacro);
                        return null;
                    }

                    // Try to get an XHTML template, our source for building nested components
                    // Fun with slashes: For some reason, Facelets really needs a slash at the start, otherwise
                    // it doesn't use my custom ResourceResolver...
                    String includePath = "/"+ pluginMacro.getMetadata().getPlugin().getPackageDefaultTemplatePath(pluginMacro.getName());
                    URL faceletURL = ResourceLoader.instance().getResource(includePath);
                    if (faceletURL == null) {
                        log.debug("macro has no default include file, not building any components: " + pluginMacro);
                        return null;
                    } else {
                        log.debug("using default template include as a resource from package: " + includePath);
                    }

                    return includePath;
                }

                private void includeMacroFacelet(WikiPluginMacro pluginMacro, String includePath, FaceletContext ctx, UIComponent parent) {
                    VariableMapper orig = ctx.getVariableMapper();
                    try {
                        log.debug("setting 'currentMacro' as an EL variable, resolves dynamically to WikiMacro instance in PAGE context");
                        ctx.setVariableMapper(new VariableMapperWrapper(orig));
                        ctx.getVariableMapper().setVariable(
                            WikiPluginMacro.CURRENT_MACRO_EL_VARIABLE,
                            Expressions.instance().createValueExpression("#{"+ pluginMacro.getPageVariableName()+"}").toUnifiedValueExpression()
                        );

                        log.debug("including macro facelets file from path: " + includePath);
                        ctx.includeFacelet(parent, includePath);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        ctx.setVariableMapper(orig);
                    }
                }


                private void includeMacroCSS(WikiPluginMacro pluginMacro, UIComponent cmp) {

                    String cssPath = "/"+ pluginMacro.getMetadata().getPlugin().getPackageCSSPath()+"/"+ pluginMacro.getName()+".css";
                    log.debug("trying to load CSS resource from classpath: " + cssPath);
                    if (ResourceLoader.instance().getResource(cssPath) != null) {
                        String cssRequestURIPath = pluginMacro.getRequestCSSPath()+"/"+ pluginMacro.getName()+".css";
                        log.debug("including macro CSS file, rendering URI for document head: " + cssRequestURIPath);

                        // Use Ajax4JSF loader, it can do what we want - add a CSS to the HTML <head>
                        HtmlLoadStyle style = new HtmlLoadStyle();
                        style.setSrc(cssRequestURIPath);
                        
                        cmp.getChildren().add(style);
                        // Clear these out in the next build phase
                        ComponentSupport.markForDeletion(style);
                    } else {
                        log.debug("no CSS resource found for macro");
                    }
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

    // Some utilities...

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

    private void setAttribute(FaceletContext ctx, UIComponent cmp, String name) {
        setAttribute(ctx, cmp, name, null);
    }

    private void setAttribute(FaceletContext ctx, UIComponent cmp, String name, Object defaultValue) {
        TagAttribute attribute = this.getAttribute(name);
        if (attribute != null) {
            Object o = attribute.getObject(ctx);
            if (o == null && defaultValue == null) {
                throw new IllegalArgumentException("Attribute '" + name + "' resolved to null and no default value specified");
            } else if (o == null) {
                cmp.getAttributes().put(name, defaultValue);
            } else {
                cmp.getAttributes().put(name, o);
            }
        }
    }

    private Stack<WikiTextMacro> getPageMacroStack() {
        if (Contexts.getPageContext().get(MACRO_STACK_PAGE_VARIABLE) == null) {
            log.debug("macro page stack is null, creating new stack for this page");
            Contexts.getPageContext().set(MACRO_STACK_PAGE_VARIABLE, new Stack<WikiTextMacro>());
        }
        return (Stack<WikiTextMacro>)Contexts.getPageContext().get(MACRO_STACK_PAGE_VARIABLE);
    }

    private boolean isMacroOnPageStack(WikiTextMacro macro) {
        Stack<WikiTextMacro> macroStack = getPageMacroStack();
        for (WikiTextMacro macroOnPageStack : macroStack) {
            if (macroOnPageStack.getName().equals(macro.getName())) return true;
        }
        return false;
    }

}
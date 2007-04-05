package org.jboss.seam.wiki.core.ui;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.FaceletContext;

import javax.faces.component.UIComponent;

import org.jboss.seam.wiki.core.action.PluginPreferenceEditor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.Component;
import org.jboss.seam.util.Resources;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;

/**
 * Includes plugin facelets at the right time, when the component tree is build by Facelets.
 *
 * @author Christian Bauer
 */
public class WikiFormattedTextComponentHandler extends ComponentHandler {

    public static final String REGEX_MACRO = Pattern.quote("[") + "<=([a-zA-Z0-9]+)" + Pattern.quote("]");

    public WikiFormattedTextComponentHandler(ComponentConfig componentConfig) {
        super(componentConfig);
    }

    protected void onComponentPopulated(FaceletContext faceletContext, UIComponent c, UIComponent parent) {
        UIWikiFormattedText component = (UIWikiFormattedText)c;

        if (component.getValue() == null) return;

        Set<String> includedMacros = new HashSet<String>();
        Matcher matcher = Pattern.compile(REGEX_MACRO).matcher((String)component.getValue());
        while (matcher.find()) {
            String macroName = matcher.group(1);

            if (includedMacros.contains(macroName)) continue;

            String includeView = "/plugins/" + macroName + "/plugin.xhtml";

            // View can't include itself
            String currentViewId = faceletContext.getFacesContext().getViewRoot().getViewId();
            if (currentViewId.equals(includeView)) continue;

            // Try to get the XHTML document
            URL includeViewURL = Resources.getResource(includeView);
            if (includeViewURL == null) continue;

            try {
                // Include plugin Facelet as a child
                faceletContext.includeFacelet(component, includeViewURL);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                includedMacros.add(macroName);
            }

            // If this plugin has preferences and editing is enabled, instantiate a
            // plugin preferences editor and put it in the PAGE context
            String pluginPreferenceName = macroName + "Preferences";
            Boolean showPluginPreferences = (Boolean)Component.getInstance("showPluginPreferences");
            Object existingEditor = Contexts.getConversationContext().get(pluginPreferenceName+"Editor");
            if ( showPluginPreferences != null && showPluginPreferences && existingEditor == null) {
                PluginPreferenceEditor pluginPreferenceEditor = new PluginPreferenceEditor(pluginPreferenceName);
                PluginPreferenceEditor.FlushObserver observer =
                        (PluginPreferenceEditor.FlushObserver)Component.getInstance("pluginPreferenceEditorFlushObserver");
                if (pluginPreferenceEditor.getPreferenceValues().size() > 0) {
                    Contexts.getConversationContext().set(pluginPreferenceName+"Editor", pluginPreferenceEditor);
                    observer.addPluginPreferenceEditor(pluginPreferenceEditor);
                }
            } else if (showPluginPreferences == null || !showPluginPreferences) {
                Contexts.getConversationContext().set(pluginPreferenceName+"Editor", null);
            }

        }
    }

}

package org.jboss.seam.wiki.core.ui;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Resources;
import org.jboss.seam.wiki.core.action.PluginPreferenceEditor;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class WikiFormattedTextHandler extends TagHandler
{

   public static final String REGEX_MACRO = Pattern.quote("[") + "<=([a-zA-Z0-9]+)"
            + Pattern.quote("]");

   private TagAttribute value;

   private Set<String> includedMacros;

   public WikiFormattedTextHandler(TagConfig config)
   {
      super(config);
      this.value = this.getRequiredAttribute("value");
   }

   public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException,
            FaceletException, ELException
   {
      includedMacros = new HashSet<String>();
      String unparsed = value.getValue(ctx);
      Matcher matcher = Pattern.compile(REGEX_MACRO).matcher(unparsed);
      int start = 0;
      this.nextHandler.apply(ctx, null);
      while (matcher.find())
      {
         if (ComponentSupport.isNew(parent))
         {
            // Include the string before the match
            UIWikiFormattedText wikiFormattedText = new UIWikiFormattedText();
            String text = unparsed.substring(start, matcher.start());
            start = matcher.end();
            wikiFormattedText.setValue(text);
            parent.getChildren().add(wikiFormattedText);
         }
         // Include the plugin
         String macroName = matcher.group(1);
         buildMacro(macroName, ctx, parent);
      }
      if (ComponentSupport.isNew(parent))
      {
         // Then include the end of the string
         UIWikiFormattedText endText = new UIWikiFormattedText();
         endText.setValue(unparsed.substring(start));
         parent.getChildren().add(endText);
      }
   }

   private void include(URL path, FaceletContext ctx, UIComponent parent) throws IOException, FaceletException,
            FacesException, ELException, IOException
   {
      // Cribbed from facelets
      VariableMapper orig = ctx.getVariableMapper();
      ctx.setVariableMapper(new VariableMapperWrapper(orig));
      try
      {
         ctx.includeFacelet(parent, path);
      }
      finally
      {
         ctx.setVariableMapper(orig);
      }
   }

   private void buildMacro(String macroName, FaceletContext ctx, UIComponent parent) throws IOException, FaceletException, FacesException, ELException, IOException
   {
      if (macroName == null || macroName.length() == 0 || includedMacros.contains(macroName))
      {
         return;
      }

      addCss(macroName, parent);
      
      String includeView = "/plugins/" + macroName + "/plugin.xhtml";
      // View can't include itself
      String currentViewId = ctx.getFacesContext().getViewRoot().getViewId();
      if (currentViewId.equals(includeView))
      {
         return;
      }
      // Try to get the XHTML document
      URL includeViewURL = Resources.getResource(includeView);
      if (includeViewURL == null)
      {
         return;
      }
      try
      {
         include(includeViewURL, ctx, parent);
      }
      finally
      {
         includedMacros.add(macroName);
      }
      createPreferencesEditor(macroName);
   }

   private void createPreferencesEditor(String macroName)
   {

      // If this plugin has preferences and editing is enabled, instantiate a
      // plugin preferences editor and put it in the PAGE context
      String pluginPreferenceName = macroName + "Preferences";
      Boolean showPluginPreferences = (Boolean) Component.getInstance("showPluginPreferences");
      Object existingEditor = Contexts.getConversationContext()
               .get(pluginPreferenceName + "Editor");
      if (showPluginPreferences != null && showPluginPreferences && existingEditor == null)
      {
         PluginPreferenceEditor pluginPreferenceEditor = new PluginPreferenceEditor(
                  pluginPreferenceName);
         PluginPreferenceEditor.FlushObserver observer = (PluginPreferenceEditor.FlushObserver) Component
                  .getInstance("pluginPreferenceEditorFlushObserver");
         if (pluginPreferenceEditor.getPreferenceValues().size() > 0)
         {
            Contexts.getConversationContext().set(pluginPreferenceName + "Editor",
                     pluginPreferenceEditor);
            observer.addPluginPreferenceEditor(pluginPreferenceEditor);
         }
      }
      else if (showPluginPreferences == null || !showPluginPreferences)
      {
         Contexts.getConversationContext().set(pluginPreferenceName + "Editor", null);
      }

   }
   
   private void addCss(String macroName, UIComponent parent) 
   {
      // Try to get the CSS for it
      WikiPreferences wikiPrefs = (WikiPreferences) Component.getInstance("wikiPreferences");
      URL css = Resources.getResource("/themes/" + wikiPrefs.getThemeName() + "/css/" + macroName + ".css");
      if (css != null)
      {
         UIStyle style = new UIStyle();
         style.setPath(css);
         parent.getChildren().add(style);
      }
   }

}
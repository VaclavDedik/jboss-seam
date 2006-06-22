package org.jboss.seam.theme;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Mutable;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Locale;

/**
 * Selects the current user's theme
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Name("themeSelector")
@Intercept(NEVER)
@Mutable
public class ThemeSelector implements Serializable
{
   private static final Log log = LogFactory.getLog(ThemeSelector.class);
   
   private String theme;
   private String[] availableThemes;
   
   @Create
   public void initDefaultTheme()
   {
      //TODO: look for a cookie
      if (availableThemes.length==0)
      {
         throw new IllegalStateException("no themes defined");
      }
      if (theme==null) 
      {
         theme = availableThemes[0];
      }
   }
   
   public void select()
   {
      Contexts.removeFromAllContexts( Seam.getComponentName(Theme.class) );
      //TODO: set the cookie
   }

   public List<SelectItem> getThemes()
   {
      List<SelectItem> selectItems = new ArrayList<SelectItem>(availableThemes.length);
      for ( String name: availableThemes )
      {
         selectItems.add( new SelectItem(name, name) ); //TODO: allow meaningful name
      }
      return selectItems;
   }

   public String getTheme()
   {
      return theme;
   }

   public void setTheme(String themeName)
   {
      this.theme = themeName;
   }

   public void setAvailableThemes(String[] themeNames)
   {
      this.availableThemes = themeNames;
   }
   
   public ResourceBundle getThemeResourceBundle()
   {
      try
      {
         java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle( 
               theme, 
               Locale.instance(), 
               Thread.currentThread().getContextClassLoader() 
            );
         log.debug("loaded resource bundle: " + theme);
         return bundle;
      }
      catch (MissingResourceException mre)
      {
         log.debug("resource bundle missing: " + theme);
         return null;
      }
   }

   public static ThemeSelector instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (ThemeSelector) Component.getInstance( Seam.getComponentName(ThemeSelector.class), ScopeType.SESSION, true );
   }

   public String[] getAvailableThemes()
   {
      return availableThemes;
   }

}

package org.jboss.seam.theme;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.AbstractMutable;
import org.jboss.seam.core.Locale;

/**
 * Selects the current user's theme
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Name("org.jboss.seam.theme.themeSelector")
@Intercept(NEVER)
public class ThemeSelector extends AbstractMutable implements Serializable
{
   private static final Log log = LogFactory.getLog(ThemeSelector.class);
   
   private String theme;
   private String[] availableThemes;
   
   private boolean cookieEnabled;
   
   @Create
   public void initDefaultTheme()
   {
      if (cookieEnabled)
      {
         Cookie cookie = (Cookie) FacesContext.getCurrentInstance().getExternalContext()
               .getRequestCookieMap().get("org.jboss.seam.core.Theme");
         if (cookie!=null) theme = cookie.getValue();
      }
      
      if (theme==null)
      {
         if (availableThemes.length==0)
         {
            throw new IllegalStateException("no themes defined");
         }
         if (theme==null) 
         {
            theme = availableThemes[0];
         }
      }
   }
   
   public void select()
   {
      Contexts.removeFromAllContexts( Seam.getComponentName(Theme.class) );
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String viewId = facesContext.getViewRoot().getViewId();
      UIViewRoot viewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, viewId);
      facesContext.setViewRoot(viewRoot);
      if (cookieEnabled)
      {
         HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
         response.addCookie( new Cookie("org.jboss.seam.core.Theme", theme) );
      }
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
      setDirty(this.theme, themeName);
      this.theme = themeName;
   }

   public void setAvailableThemes(String[] themeNames)
   {
      setDirty(this.availableThemes, themeNames);
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
      return (ThemeSelector) Component.getInstance(ThemeSelector.class, ScopeType.SESSION);
   }

   public String[] getAvailableThemes()
   {
      return availableThemes;
   }

   public boolean isCookieEnabled()
   {
      return cookieEnabled;
   }

   public void setCookieEnabled(boolean cookieEnabled)
   {
      setDirty(this.cookieEnabled, cookieEnabled);
      this.cookieEnabled = cookieEnabled;
   }

}

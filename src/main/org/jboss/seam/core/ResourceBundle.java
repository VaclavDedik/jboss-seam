package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.Locale;
import java.util.MissingResourceException;

import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;

/**
 * Support for an application-global resource bundle
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Intercept(NEVER)
@Name("resourceBundle")
@Startup
public class ResourceBundle {
   private static final Logger log = Logger.getLogger(ResourceBundle.class);

   private String bundleName = "messages";
   private java.util.ResourceBundle bundle;

   public String getBundleName() 
   {
      return bundleName;
   }
   
   public void setBundleName(String bundleName) 
   {
      this.bundleName = bundleName;
   }
   
   @Create
   public void loadBundle() 
   {
      try
      {
         bundle = searchForBundle();
         log.info("loaded resource bundle: " + bundleName);
      }
      catch (MissingResourceException mre)
      {
         log.info("resource bundle missing: " + bundleName);
      }
   }

   private java.util.ResourceBundle getBundle(Locale locale) {
      return java.util.ResourceBundle.getBundle( bundleName, locale, Thread.currentThread().getContextClassLoader() );
   }
   
   private java.util.ResourceBundle searchForBundle()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (facesContext!=null)
      {
         Locale requestLocale = facesContext.getExternalContext().getRequestLocale();
         if (requestLocale!=null) return getBundle( requestLocale );
         Locale defaultLocale = facesContext.getApplication().getDefaultLocale();
         if (defaultLocale!=null) return getBundle( defaultLocale );
      }
      return getBundle( Locale.getDefault() );
   }
   
   @Unwrap
   public java.util.ResourceBundle getBundle()
   {
      return bundle;
   }
   
   public static java.util.ResourceBundle instance()
   {
      return (java.util.ResourceBundle) Component.getInstance( Seam.getComponentName(ResourceBundle.class), true );
   }
}

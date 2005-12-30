package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.Locale;
import java.util.MissingResourceException;

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
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("resourceBundle")
@Startup
public class ResourceBundle {
   private static final Logger log = Logger.getLogger(ResourceBundle.class);

   private String bundleName = "messages";
   private String language;
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
         if (language==null) 
         {
            bundle = java.util.ResourceBundle.getBundle( bundleName, Locale.getDefault(), Thread.currentThread().getContextClassLoader() );
         }
         else
         {
            bundle = java.util.ResourceBundle.getBundle( bundleName, new Locale(language), Thread.currentThread().getContextClassLoader() );
         }
         log.info("loaded resource bundle: " + bundleName);
      }
      catch (MissingResourceException mre)
      {
         log.info("resource bundle missing: " + bundleName);
      }
   }
   
   @Unwrap
   public java.util.ResourceBundle getBundle()
   {
      return bundle;
   }

   public String getLanguage() 
   {
      return language;
   }

   public void setLanguage(String language) 
   {
      this.language = language;
   }
   
   public static java.util.ResourceBundle instance()
   {
      return (java.util.ResourceBundle) Component.getInstance( Seam.getComponentName(ResourceBundle.class), true );
   }
}

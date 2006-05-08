package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Support for an application-global resource bundle
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Intercept(NEVER)
@Name("resourceBundle")
public class ResourceBundle implements Serializable {
   
   private static final Log log = LogFactory.getLog(ResourceBundle.class);

   private String bundleName = "messages";
   private transient java.util.ResourceBundle bundle;

   public String getBundleName() 
   {
      return bundleName;
   }
   
   public void setBundleName(String bundleName) 
   {
      this.bundleName = bundleName;
   }
   
   private void loadBundle() 
   {
      try
      {
         bundle = java.util.ResourceBundle.getBundle( 
               bundleName, 
               Locale.instance(), 
               Thread.currentThread().getContextClassLoader() 
            );
         log.debug("loaded resource bundle: " + bundleName);
      }
      catch (MissingResourceException mre)
      {
         log.debug("resource bundle missing: " + bundleName);
      }
   }

   @Unwrap
   public java.util.ResourceBundle getBundle()
   {
      if (bundle==null) loadBundle();
      return bundle;
   }
   
   public static java.util.ResourceBundle instance()
   {
      return (java.util.ResourceBundle) Component.getInstance( Seam.getComponentName(ResourceBundle.class), true );
   }
}

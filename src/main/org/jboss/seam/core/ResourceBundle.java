package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
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

   private String[] bundleNames = {"messages"};
   private transient java.util.ResourceBundle bundle;

   public String[] getBundleNames() 
   {
      return bundleNames;
   }
   
   public void setBundleNames(String[] bundleNames) 
   {
      this.bundleNames = bundleNames;
   }
   
   private java.util.ResourceBundle loadBundle(String bundleName) 
   {
      try
      {
         java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle( 
               bundleName, 
               Locale.instance(), 
               Thread.currentThread().getContextClassLoader() 
            );
         log.debug("loaded resource bundle: " + bundleName);
         return bundle;
      }
      catch (MissingResourceException mre)
      {
         log.debug("resource bundle missing: " + bundleName);
         return null;
      }
   }
   
   private void createUberBundle()
   {
      if ( bundleNames!=null && bundleNames.length>0 )
      {
      
         final java.util.ResourceBundle[] littleBundles = new java.util.ResourceBundle[bundleNames.length];
         for (int i=0; i<bundleNames.length; i++)
         {
            littleBundles[i] = loadBundle( bundleNames[i] );
         }
         
         bundle = new java.util.ResourceBundle()
         {
   
            @Override
            public java.util.Locale getLocale()
            {
               return littleBundles[0].getLocale();
            }

            @Override
            public Enumeration<String> getKeys()
            {
               throw new UnsupportedOperationException();
            }
   
            @Override
            protected Object handleGetObject(String key)
            {
               for (java.util.ResourceBundle littleBundle: littleBundles)
               {
                  try
                  {
                     return littleBundle.getObject(key);
                  }
                  catch (MissingResourceException mre) {}
               }
               throw new MissingResourceException("Can't find resource in bundles: " + key, getClass().getName(), key );
            }
            
         };
         
      }
   }

   @Unwrap
   public java.util.ResourceBundle getBundle()
   {
      if (bundle==null) createUberBundle();
      return bundle;
   }
   
   public static java.util.ResourceBundle instance()
   {
      return (java.util.ResourceBundle) Component.getInstance( Seam.getComponentName(ResourceBundle.class), true );
   }
}

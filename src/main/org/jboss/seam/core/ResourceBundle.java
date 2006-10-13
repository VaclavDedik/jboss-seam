package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
   
   @Deprecated
   public void setBundleName(String bundleName)
   {
      bundleNames = new String[] { bundleName };
   }
   
   @Deprecated
   public String getBundleName()
   {
      return bundleNames==null || bundleNames.length==0 ? null : bundleNames[0];
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
      if (bundleNames!=null)
      {
         
         final List<java.util.ResourceBundle> littleBundles = new ArrayList<java.util.ResourceBundle>(bundleNames.length);
         for (String bundleName: bundleNames)
         {
            java.util.ResourceBundle littleBundle = loadBundle(bundleName);
            if (littleBundle!=null) littleBundles.add(littleBundle);
         }
         
         if ( !littleBundles.isEmpty() )
         {
            bundle = new java.util.ResourceBundle()
            {
      
               @Override
               public java.util.Locale getLocale()
               {
                  return Locale.instance();
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
                     if (littleBundle!=null)
                     {
                        try
                        {
                           return littleBundle.getObject(key);
                        }
                        catch (MissingResourceException mre) {}
                     }
                  }
                  throw new MissingResourceException("Can't find resource in bundles: " + key, getClass().getName(), key );
               }
               
            };
         }
         
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

package org.jboss.seam.core;

import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.util.EnumerationEnumeration;
import org.jboss.seam.util.Strings;

/**
 * Factory for a session-scoped localized resource bundle
 * that searches for resources in delegate resource bundles
 * specified in pages.xml, and a configurable list of 
 * delegate resource bundles. 
 * 
 * @author Gavin King
 */
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Name("org.jboss.seam.core.resourceBundleFactory")
@Install(precedence=BUILT_IN)
public class ResourceBundle 
{
   
   protected java.util.Locale getCurrentLocale()
   {
      //TODO:
      return Locale.getDefault();
   }
   
   public class UberResourceBundle extends java.util.ResourceBundle
   {
      private final List<java.util.ResourceBundle> bundles;

      public UberResourceBundle(List<java.util.ResourceBundle> bundles)
      {
         this.bundles = bundles;
      }

      @Override
      public java.util.Locale getLocale()
      {
         return getCurrentLocale();
      }

      @Override
      public Enumeration<String> getKeys()
      {
         List<java.util.ResourceBundle> pageBundles = getPageResourceBundles();
         Enumeration<String>[] enumerations = new Enumeration[ bundles.size() + pageBundles.size() ];
         int i=0;
         for (; i<pageBundles.size(); i++)
         {
            enumerations[i++] = pageBundles.get(i).getKeys();
         }
         for (; i<bundles.size(); i++)
         {
            enumerations[i] = bundles.get(i).getKeys();
         }
         return new EnumerationEnumeration<String>(enumerations);
      }

      @Override
      protected Object handleGetObject(String key)
      {
         List<java.util.ResourceBundle> pageBundles = getPageResourceBundles();
         for (java.util.ResourceBundle pageBundle: pageBundles)
         {
            try
            {
               return pageBundle.getObject(key);
            }
            catch (MissingResourceException mre) {}
         }
         
         for (java.util.ResourceBundle littleBundle: bundles)
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
         
         return null; //superclass is responsible for throwing MRE
      }

      private List<java.util.ResourceBundle> getPageResourceBundles()
      {
         String viewId = Pages.getCurrentViewId();
         if (viewId!=null)
         {
            return Pages.instance().getResourceBundles(viewId);
         }
         else
         {
            return Collections.EMPTY_LIST;
         }
      }
   }

   private static final long serialVersionUID = -3236251335438092538L;
   private static final LogProvider log = Logging.getLogProvider(ResourceBundle.class);

   private String[] bundleNames = {"messages"};

   /**
    * The configurable list of delegate resource bundle names
    * 
    * @return an array of resource bundle names
    */
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
      bundleNames = bundleName==null ? null : new String[] { bundleName };
   }
   
   @Deprecated
   public String getBundleName()
   {
      return bundleNames==null || bundleNames.length==0 ? null : bundleNames[0];
   }
   
   /**
    * Load a resource bundle by name (may be overridden by subclasses
    * who want to use non-standard resource bundle types).
    * 
    * @param bundleName the name of the resource bundle
    * @return an instance of java.util.ResourceBundle
    */
   protected java.util.ResourceBundle loadBundle(String bundleName) 
   {
      try
      {
         java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle( 
               bundleName, 
               getCurrentLocale(), 
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
   
   protected java.util.ResourceBundle createUberBundle()
   {
      final List<java.util.ResourceBundle> littleBundles = new ArrayList<java.util.ResourceBundle>();
      if (bundleNames!=null)
      {  
         for (String bundleName: bundleNames)
         {
            java.util.ResourceBundle littleBundle = loadBundle(bundleName);
            if (littleBundle!=null) littleBundles.add(littleBundle);
         }
      }
      
      java.util.ResourceBundle validatorBundle = loadBundle("ValidatorMessages");
      if (validatorBundle!=null) littleBundles.add(validatorBundle);
      java.util.ResourceBundle validatorDefaultBundle = loadBundle("org/hibernate/validator/resources/DefaultValidatorMessages");
      if (validatorDefaultBundle!=null) littleBundles.add(validatorDefaultBundle);
         
      return new UberResourceBundle(littleBundles);
   }

   /**
    * Create a ResourceBundle in the session scope. The session scope is used because
    * creating the bundle is somewhat expensive, so it can be cached there because
    * the session Locale changes infrequently. When the Locale is changed, LocaleSelector
    * is responsible for removing the ResourceBundle from the session context.
    * 
    * @return a ResourceBundle that wraps all the delegate bundles
    */
   @Factory(value="org.jboss.seam.core.resourceBundle", autoCreate=true, scope=SESSION)
   public java.util.ResourceBundle getBundle()
   {
      return createUberBundle();
   }
   
   @Override
   public String toString()
   {
      String concat = bundleNames==null ? "" : Strings.toString( ", ", (Object[]) bundleNames );
      return "ResourceBundle(" + concat + ")";
   }

   /**
    * @return the ResourceBundle instance
    */
   public static java.util.ResourceBundle instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("no session context active");
      }
      return (java.util.ResourceBundle) Component.getInstance("org.jboss.seam.core.resourceBundle", true);
   }
   
}

package org.jboss.seam.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.util.EnumerationEnumeration;

/**
 * The Seam resource bundle which searches for resources in delegate 
 * resource bundles specified in pages.xml, and a configurable list of 
 * delegate resource bundles specified in components.xml.
 * 
 * @see ResourceLoader
 * @author Gavin King
 *
 */
public class SeamResourceBundle extends java.util.ResourceBundle
{
   private final List<java.util.ResourceBundle> bundles = new ArrayList<java.util.ResourceBundle>();
   private boolean initialized;
   
   /**
    * Get an instance for the current Seam Locale
    * 
    * @see Locale
    * 
    * @return a SeamResourceBundle
    */
   public static java.util.ResourceBundle getBundle()
   {
      return java.util.ResourceBundle.getBundle( SeamResourceBundle.class.getName(), Locale.instance() );
   }
   
   private void init()
   {
      if ( !initialized && Contexts.isApplicationContextActive() )
      {
         ResourceLoader instance = ResourceLoader.instance();
         if (instance.getBundleNames()!=null)
         {  
            for ( String bundleName: instance.getBundleNames() )
            {
               java.util.ResourceBundle littleBundle = instance.loadBundle(bundleName);
               if (littleBundle!=null) bundles.add(littleBundle);
            }
         }
         
         java.util.ResourceBundle validatorBundle = instance.loadBundle("ValidatorMessages");
         if (validatorBundle!=null) bundles.add(validatorBundle);
         java.util.ResourceBundle validatorDefaultBundle = instance.loadBundle("org/hibernate/validator/resources/DefaultValidatorMessages");
         if (validatorDefaultBundle!=null) bundles.add(validatorDefaultBundle);
         java.util.ResourceBundle facesBundle = instance.loadBundle("javax.faces.Messages"); //ie. FacesMessage.FACES_MESSAGES;
         if (facesBundle!=null) bundles.add(facesBundle);
         
         initialized = true;
      }
   }
   
   @Override
   public Enumeration<String> getKeys()
   {
      init();
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
      init();
      List<java.util.ResourceBundle> pageBundles = getPageResourceBundles();
      for (java.util.ResourceBundle pageBundle: pageBundles)
      {
         try
         {
            return interpolate( pageBundle.getObject(key) );
         }
         catch (MissingResourceException mre) {}
      }
      
      for (java.util.ResourceBundle littleBundle: bundles)
      {
         if (littleBundle!=null)
         {
            try
            {
               return interpolate( littleBundle.getObject(key) );
            }
            catch (MissingResourceException mre) {}
         }
      }
      
      return null; //superclass is responsible for throwing MRE
   }
   
   private Object interpolate(Object message)
   {
      return message!=null && message instanceof String ?
               Interpolator.instance().interpolate( (String) message ) :
               message;
   }

   private List<java.util.ResourceBundle> getPageResourceBundles()
   {
      //TODO: oops! A hard dependency to JSF!
      String viewId = Pages.getCurrentViewId();
      if (viewId!=null)
      {
         //we can't cache these bundles, since the viewId
         //may change in the middle of a request
         return Pages.instance().getResourceBundles(viewId);
      }
      else
      {
         return Collections.EMPTY_LIST;
      }
   }
   
}
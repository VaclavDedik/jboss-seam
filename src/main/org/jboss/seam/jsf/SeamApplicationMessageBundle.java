/**
 * 
 */
package org.jboss.seam.jsf;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.util.IteratorEnumeration;

/**
 * Adaptor allow use of #{messages} for JSF application messages - 
 * for example built-in validators and converters
 *
 */
public class SeamApplicationMessageBundle extends ResourceBundle
{

   private ResourceBundle getBundle()
   {
      return Contexts.isApplicationContextActive() && Contexts.isSessionContextActive() ?
               org.jboss.seam.core.ResourceBundle.instance() : null;
   }

   @Override
   public Enumeration<String> getKeys()
   {
      ResourceBundle bundle = getBundle();
      return bundle==null ? 
            new IteratorEnumeration( Collections.EMPTY_LIST.iterator() ) :
            bundle.getKeys();
   }

   @Override
   protected Object handleGetObject(String key)
   {
      ResourceBundle bundle = getBundle();
      if (bundle==null)
      {
         return null;
      }
      else
      {
         Object resource = bundle.getObject(key);
         return resource!=null && ( resource instanceof String ) ?
               Interpolator.instance().interpolate( (String) resource ) :
               resource;
      }
   }

}
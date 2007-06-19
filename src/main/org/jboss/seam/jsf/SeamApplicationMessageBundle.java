/**
 * 
 */
package org.jboss.seam.jsf;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.jboss.seam.core.Interpolator;

/**
 * Adaptor allow use of #{messages} for JSF application messages - 
 * for example built-in validators and converters
 *
 */
public class SeamApplicationMessageBundle extends ResourceBundle
{

   @Override
   public Enumeration<String> getKeys()
   {
      return org.jboss.seam.international.ResourceBundle.instance().getKeys();
   }

   @Override
   protected Object handleGetObject(String key)
   {
      Object resource = org.jboss.seam.international.ResourceBundle.instance().getObject(key);
      return resource!=null && ( resource instanceof String ) ?
            Interpolator.instance().interpolate( (String) resource ) :
            resource;
   }

}
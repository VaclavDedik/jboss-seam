package org.jboss.seam.international;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Interpolator;

/**
 * Support for an application-global resource bundle
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Name("org.jboss.seam.international.messages")
@Install(precedence=BUILT_IN)
public class Messages implements Serializable 
{
   private static final long serialVersionUID = 1292464253307553295L;
   
   private transient Map<String, String> messages;
   
   private void init() 
   {  
      messages = new AbstractMap<String, String>()
      {
         private java.util.ResourceBundle bundle = ResourceBundle.instance();

         @Override
         public String get(Object key) 
         {
            if (key instanceof String)
            {
               String resourceKey = (String) key;
               String resource=null;
               if (bundle!=null)
               {
                  try
                  {
                     resource = bundle.getString(resourceKey);
                  }
                  catch (MissingResourceException mre)
                  {
                     //Just swallow
                  }
               }
               return resource==null ?
                     resourceKey :
                     Interpolator.instance().interpolate(resource);
            }
            else
            {
               return null;
            }
         }
         
         @Override
         public Set<Map.Entry<String, String>> entrySet() 
         {
            Enumeration<String> keys = bundle.getKeys();
            Map<String, String> map = new HashMap<String, String>();
            while ( keys.hasMoreElements() )
            {
               String key = keys.nextElement();
               map.put( key, get(key) );
            }
            return map.entrySet();
         }
         
      };
   }

   @Unwrap
   public Map<String, String> getMessages()
   {
      if (messages==null) init();
      return messages;
   }
   
   public static Map<String, String> instance()
   {
      return (Map<String, String>) Component.getInstance(Messages.class, true );
   }
}

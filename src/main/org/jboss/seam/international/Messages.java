package org.jboss.seam.international;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.ResourceBundle;

/**
 * Factory for a Map that interpolates messages defined in the
 * Seam ResourceBundle.
 * 
 * @see org.jboss.seam.core.ResourceBundle
 * 
 * @author Gavin King
 */
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Name("org.jboss.seam.international.messagesFactory")
@Install(precedence=BUILT_IN)
public class Messages
{
   //TODO: now we have ELResolver, it doesn't *have* to be a Map...
      
   protected Map createMap() 
   {  
      return new AbstractMap<String, String>()
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

   /**
    * Create the Map and cache it in the EVENT scope. No need to cache
    * it in the SESSION scope, since it is inexpensive to create.
    * 
    * @return a Map that interpolates messages in the Seam ResourceBundle
    */
   @Factory(value="org.jboss.seam.international.messages", autoCreate=true, scope=EVENT)
   public Map<String, String> getMessages()
   {
      return createMap();
   }
   
   /**
    * @return the message Map instance
    */
   public static Map<String, String> instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("no event context active");
      }
      return (Map<String, String>) Component.getInstance("org.jboss.seam.international.messages", true);
   }
}

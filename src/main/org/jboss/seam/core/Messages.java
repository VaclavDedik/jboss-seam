package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Template;

/**
 * Support for an application-global resource bundle
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Intercept(NEVER)
@Name("messages")
public class Messages {
   
   private Map<String, String> messages;
   
   @Create
   public void init() 
   {
      messages = new AbstractMap<String, String>()
      {
         private java.util.ResourceBundle bundle = ResourceBundle.instance();
         private Map<String, String> cache = new HashMap<String, String>();

         @Override
         public String get(Object key) {
            if (key instanceof String)
            {
               String resourceKey = (String) key;
               String cachedValue = cache.get(key);
               if (cachedValue==null)
               {
                  String resource = bundle.getString(resourceKey);
                  if (resource==null)
                  {
                     return resourceKey;
                  }
                  else
                  {
                     String result = Template.render(resource);
                     cache.put(resourceKey, result);
                     return result;
                  }
               }
               else
               {
                  return cachedValue;
               }
            }
            else
            {
               return null;
            }
         }
         
         @Override
         public Set<Map.Entry<String, String>> entrySet() {
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
   public Map getMessages()
   {
      return messages;
   }
   
   public static Map instance()
   {
      return (Map) Component.getInstance( Seam.getComponentName(Messages.class), true );
   }
}

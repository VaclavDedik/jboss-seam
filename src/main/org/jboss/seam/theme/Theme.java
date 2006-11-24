package org.jboss.seam.theme;

import static org.jboss.seam.InterceptionType.NEVER;
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
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.core.Interpolator;

/**
 * Support for an application-global resource bundle
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Intercept(NEVER)
@Name("org.jboss.seam.theme.theme")
@Install(precedence=BUILT_IN)
public class Theme implements Serializable {
   
   private transient Map messages;
   
   private void init() 
   {
      final java.util.ResourceBundle bundle = ThemeSelector.instance().getThemeResourceBundle();
      if (bundle!=null) 
      {
      
         messages = new AbstractMap<String, String>()
         {
            //private Map<String, String> cache = new HashMap<String, String>();
   
            @Override
            public String get(Object key) {
               if (key instanceof String)
               {
                  String resourceKey = (String) key;
                  /*String cachedValue = cache.get(key);
                  if (cachedValue==null)
                  {*/
                     String resource;
                     try
                     {
                        resource = bundle.getString(resourceKey);
                     }
                     catch (MissingResourceException mre)
                     {
                        return resourceKey;
                     }
                     if (resource==null)
                     {
                        return resourceKey;
                     }
                     else
                     {
                        //cache.put(resourceKey, resource);
                        return Interpolator.instance().interpolate(resource);
                     }
                  /*}
                  else
                  {
                     return Interpolator.instance().interpolate(cachedValue);
                  }*/
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
   }
   
   @Unwrap
   public java.util.Map getTheme()
   {
      if (messages==null) init();
      return messages;
   }
   
   public static java.util.Map instance()
   {
      return (java.util.Map) Component.getInstance(Theme.class, true );
   }
}

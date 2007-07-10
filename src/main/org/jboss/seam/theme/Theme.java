package org.jboss.seam.theme;

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
import org.jboss.seam.core.Interpolator;

/**
 * Factory for a Map of resources that may be
 * used for skinning the user interface.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Name("org.jboss.seam.theme.themeFactory")
@Install(precedence=BUILT_IN)
public class Theme
{
   
   protected Map<String, String> createMap() 
   {
      final java.util.ResourceBundle bundle = ThemeSelector.instance().getThemeResourceBundle();
      if (bundle!=null) 
      {
      
         return new AbstractMap<String, String>()
         {
            //private Map<String, String> cache = new HashMap<String, String>();
   
            @Override
            public String get(Object key) 
            {
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
      else
      {
         return null;
      }
   }
   
   /**
    * Create a Map in the event scope. When the theme is changed, ThemeSelector
    * is responsible for removing the Map from the event context.
    * 
    */
   @Factory(value="org.jboss.seam.theme.theme", autoCreate=true, scope=EVENT)
   public java.util.Map getTheme()
   {
      return createMap();
   }
   
   public static java.util.Map instance()
   {
      return (java.util.Map) Component.getInstance("org.jboss.seam.theme.theme", true);
   }
}

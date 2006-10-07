package org.jboss.seam.core;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;

@Name("param")
@Intercept(InterceptionType.NEVER)
public class RenderParameters
{
   private Map<String, Object> parameters = new HashMap<String, Object>();

   @Unwrap
   public Map<String, Object> getParameters()
   {
      return new AbstractMap<String, Object>()
      {
         
         private Map requestParameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

         @Override
         public Set<Entry<String, Object>> entrySet()
         {
            return parameters.entrySet();
         }

         @Override
         public Object get(Object key)
         {
            if ( parameters.containsKey(key) )
            {
               return parameters.get(key);
            }
            else if ( requestParameters.containsKey(key) )
            {
               return requestParameters.get(key);
            }
            else
            {
               Map<String, Object> pageParameters = (Map<String, Object>) Contexts.getPageContext().get("pageParameters");
               return pageParameters==null ? null : pageParameters.get(key);
            }
         }

         @Override
         public Object put(String key, Object value)
         {
            Object old = get(key);
            parameters.put(key, value);
            return old;
         }
         
      };
   }
   
   public static Map<String, Object> instance()
   {
      return (Map<String, Object>) Component.getInstance(RenderParameters.class, false);
   }
   
}

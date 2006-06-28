package org.jboss.seam.core;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;

@Name("renderParameters")
@Intercept(InterceptionType.NEVER)
public class RenderParameters
{
   private Map<String, Object> parameters = new HashMap<String, Object>();

   @Unwrap
   public Map<String, Object> getParameters()
   {
      if ( Manager.instance().isNonFacesRequest() )
      {
         return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
      }
      else
      {
         return parameters;
      }
   }
   
   public static Map<String, Object> instance()
   {
      return (Map<String, Object>) Component.getInstance(RenderParameters.class, false);
   }
   
}

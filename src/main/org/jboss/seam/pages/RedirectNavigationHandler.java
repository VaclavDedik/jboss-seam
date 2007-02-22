/**
 * 
 */
package org.jboss.seam.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public final class RedirectNavigationHandler extends NavigationHandler
{
   private final String viewId;
   private final List<Param> params;
   private final String message;
   private final Severity severity;

   public RedirectNavigationHandler(String viewId, List<Param> params, String message, Severity severity)
   {
      this.viewId = viewId;
      this.params = params;
      this.message = message;
      this.severity = severity;
   }

   @Override
   public boolean navigate(FacesContext context)
   {
      addFacesMessage(message, severity);
      
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( Param parameter: params )
      {
         Object value = parameter.getValueFromModel(context);
         //render it even if the value is null, since we want it
         //to override page parameter values which would be
         //appended by the redirect filter
         //if (value!=null)
         //{
            parameters.put( parameter.getName(), value );
         //}
      }
      
      redirect(viewId, parameters);
      return true;
   }

}
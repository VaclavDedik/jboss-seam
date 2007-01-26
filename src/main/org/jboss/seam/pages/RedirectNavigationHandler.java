/**
 * 
 */
package org.jboss.seam.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

public final class RedirectNavigationHandler extends NavigationHandler
{
   private final String viewId;
   private final List<Param> params;

   public RedirectNavigationHandler(String viewId, List<Param> params)
   {
      this.viewId = viewId;
      this.params = params;
   }

   @Override
   public boolean navigate(FacesContext context)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( Param pageParameter: params )
      {
         Object value = pageParameter.getValueFromModel(context);
         //render it even if the value is null, since we want it
         //to override page parameter values which would be
         //appended by the redirect filter
         //if (value!=null)
         //{
            parameters.put( pageParameter.getName(), value );
         //}
      }
      redirect(viewId, parameters);
      return true;
   }
}
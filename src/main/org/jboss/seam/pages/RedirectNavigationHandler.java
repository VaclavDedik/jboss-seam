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
   public void navigate(FacesContext context)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( Param pageParameter: params )
      {
         parameters.put( pageParameter.getName(), pageParameter.getValueFromModel(context) );
      }
      redirect(viewId, parameters);
   }
}
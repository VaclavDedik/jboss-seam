/**
 * 
 */
package org.jboss.seam.pages;

import javax.faces.context.FacesContext;

public final class RenderNavigationHandler extends NavigationHandler
{
   private final String viewId;
   private final String message;

   public RenderNavigationHandler(String viewId, String message)
   {
      this.viewId = viewId;
      this.message = message;
   }

   @Override
   public boolean navigate(FacesContext context)
   {
      addFacesMessage(message);
      render(viewId);
      return true;
   }
}
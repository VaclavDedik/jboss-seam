/**
 * 
 */
package org.jboss.seam.pages;

import javax.faces.context.FacesContext;

public final class RenderNavigationHandler extends NavigationHandler
{
   private final String viewId;

   public RenderNavigationHandler(String viewId)
   {
      this.viewId = viewId;
   }

   @Override
   public boolean navigate(FacesContext context)
   {
      render(viewId);
      return true;
   }
}
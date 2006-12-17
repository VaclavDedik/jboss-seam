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
   public void navigate(FacesContext context)
   {
      render(viewId);
   }
}
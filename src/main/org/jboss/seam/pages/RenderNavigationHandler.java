/**
 * 
 */
package org.jboss.seam.pages;

import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public final class RenderNavigationHandler extends NavigationHandler
{
   private final String viewId;
   private final String message;
   private final Severity severity;

   public RenderNavigationHandler(String viewId, String message, Severity severity)
   {
      this.viewId = viewId;
      this.message = message;
      this.severity = severity;
   }

   @Override
   public boolean navigate(FacesContext context)
   {
      addFacesMessage(message, severity);
      render(viewId);
      return true;
   }
}
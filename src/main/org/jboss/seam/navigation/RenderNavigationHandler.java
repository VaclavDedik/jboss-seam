package org.jboss.seam.navigation;

import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

/**
 * Metadata for a &lt;render/&gt; in pages.xml
 * 
 * @author Gavin King
 *
 */
public final class RenderNavigationHandler extends NavigationHandler
{
   private final String viewId;
   private final String message;
   private final Severity severity;
   private final String control;

   public RenderNavigationHandler(String viewId, String message, Severity severity, String control)
   {
      this.viewId = viewId;
      this.message = message;
      this.severity = severity;
      this.control = control;
   }

   @Override
   public boolean navigate(FacesContext context)
   {
      addFacesMessage(message, severity, control);
      render(viewId);
      return true;
   }
}
package org.jboss.seam.core;

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

public abstract class Navigator
{
   private static final LogProvider log = Logging.getLogProvider(Navigator.class);

   /**
    * Send an error.
    */
   protected void error(int code, String message)
   {
      if ( log.isDebugEnabled() ) log.debug("sending error: " + code);
      org.jboss.seam.core.HttpError httpError = org.jboss.seam.core.HttpError.instance();
      if (message==null)
      {
         httpError.send(code);
      }
      else
      {
         httpError.send(code, message);
      }
   }

   /**
    * Redirect to the view id.
    */
   protected void redirect(String viewId, Map<String, Object> parameters)
   {
      if ( Strings.isEmpty(viewId) )
      {
         viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
      }
      if ( log.isDebugEnabled() ) log.debug("redirecting to: " + viewId);
      Manager.instance().redirect(viewId, parameters, true);
   }
   
   /**
    * Render the view id.
    */
   protected void render(String viewId)
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if ( !Strings.isEmpty(viewId) )
      {
         UIViewRoot viewRoot = facesContext.getApplication().getViewHandler()
               .createView(facesContext, viewId);
         facesContext.setViewRoot(viewRoot);
      }
      else
      {
         viewId = facesContext.getViewRoot().getViewId(); //just for the log message
      }
      if ( log.isDebugEnabled() ) log.debug("rendering: " + viewId);
      facesContext.renderResponse();
   }

}

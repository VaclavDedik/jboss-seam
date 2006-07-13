//$Id$
package org.jboss.seam.interceptors;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.HttpError;
import org.jboss.seam.annotations.Redirect;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.jsf.AbstractSeamPhaseListener;

/**
 * Handle exceptions
 * 
 * @author Gavin King
 */
@Around({ConversationalInterceptor.class, RemoveInterceptor.class, BijectionInterceptor.class})
public class ExceptionInterceptor extends AbstractInterceptor
{

   private static final Log log = LogFactory.getLog(ExceptionInterceptor.class);

   @AroundInvoke
   public Object handleExceptions(InvocationContext invocation) throws Exception
   {
      boolean outermost = invocation.getContextData().get("org.jboss.seam.outermostExceptionInterceptor")==null;
      invocation.getContextData().put("org.jboss.seam.outermostExceptionInterceptor", true);
      try
      {
         return invocation.proceed();
      }
      catch (Exception e)
      {
         if ( outermost && FacesContext.getCurrentInstance()!=null )
         {
            if ( e.getClass().isAnnotationPresent(Redirect.class) )
            {
               redirect( e.getClass().getAnnotation(Redirect.class).viewId() );
               handled(e);
            }
            else if ( e.getClass().isAnnotationPresent(HttpError.class) )
            {
               error( e.getClass().getAnnotation(HttpError.class).errorCode(), e.getMessage() );
               handled(e);
            }
            else if ( Init.instance().isDebug() && 
                      !e.getClass().isAnnotationPresent(javax.ejb.ApplicationException.class) )
            {
               redirectToDebugPage(e);
               handled(e);
            }
         }
         throw e;
      }
   }

   private void error(int code, String message)
   {
      if ( log.isDebugEnabled() ) log.debug("sending error: " + code);
      FacesContext facesContext = FacesContext.getCurrentInstance();
      org.jboss.seam.core.HttpError httpError = org.jboss.seam.core.HttpError.instance();
      httpError.send(code, message);
      FacesMessages.afterPhase();
      AbstractSeamPhaseListener.storeAnyConversationContext(facesContext);
   }

   private void redirect(String viewId)
   {
      if ( log.isDebugEnabled() ) log.debug("redirecting to: " + viewId);
      FacesContext facesContext = FacesContext.getCurrentInstance();
      org.jboss.seam.core.Redirect redirect = org.jboss.seam.core.Redirect.instance();
      redirect.setViewId(viewId);
      redirect.execute();
      FacesMessages.afterPhase();
      AbstractSeamPhaseListener.storeAnyConversationContext(facesContext);
   }

   private void redirectToDebugPage(Exception e)
   {
      log.error("redirecting to debug page", e);
      Contexts.getConversationContext().set("org.jboss.seam.debug.lastException", e);
      Contexts.getConversationContext().set("org.jboss.seam.debug.phaseId", Lifecycle.getPhaseId().toString());
      FacesContext facesContext = FacesContext.getCurrentInstance();
      org.jboss.seam.core.Redirect redirect = org.jboss.seam.core.Redirect.instance();
      redirect.setViewId("/debug.xhtml");
      Manager manager = Manager.instance();
      manager.beforeRedirect();
      redirect.setParameter( manager.getConversationIdParameter(), manager.getCurrentConversationId() );
      redirect.execute();
      FacesMessages.afterPhase();
      AbstractSeamPhaseListener.storeAnyConversationContext(facesContext);
   }

   private static void handled(Exception e)
   {
      getRequest().put("org.jboss.seam.exceptionHandled", e);
   }

   private static Map getRequest()
   {
      return FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
   }
   
}

//$Id$
package org.jboss.seam.interceptors;

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.HttpError;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.Redirect;
import org.jboss.seam.annotations.Render;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Manager;
import org.jboss.seam.jsf.AbstractSeamPhaseListener;

/**
 * Handle exceptions
 * 
 * @author Gavin King
 */
@Interceptor(around={ConversationalInterceptor.class, RemoveInterceptor.class, BijectionInterceptor.class})
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
               Redirect redirect = e.getClass().getAnnotation(Redirect.class);
               addFacesMessage( e, redirect.message() );
               redirect( redirect.viewId() );
               handled(e);
            }
            else if ( e.getClass().isAnnotationPresent(Render.class) )
            {
               if ( Lifecycle.getPhaseId()!=PhaseId.INVOKE_APPLICATION )
               {
                  //unfortunately, @Render can only really work during an action invocation
                  throw e;
               }
               else
               {
                  Render render = e.getClass().getAnnotation(Render.class);
                  addFacesMessage( e, render.message() );
                  render( render.viewId() );
                  return null;
               }
            }
            else if ( e.getClass().isAnnotationPresent(HttpError.class) )
            {
               HttpError httpError = e.getClass().getAnnotation(HttpError.class);
               error( httpError.errorCode(), renderExceptionMessage( e, httpError.message() ) );
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

   private void addFacesMessage(Exception e, String message)
   {
      FacesMessages.instance().add( renderExceptionMessage(e, message) );
   }
   
   private String renderExceptionMessage(Exception e, String message)
   {
      return Interpolator.instance()
            .interpolate( "".equals(message) ? e.getMessage() : message );
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
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if ( "".equals(viewId) )
      {
         viewId = facesContext.getViewRoot().getViewId();
      }
      if ( log.isDebugEnabled() ) log.debug("redirecting to: " + viewId);
      org.jboss.seam.core.Redirect redirect = org.jboss.seam.core.Redirect.instance();
      redirect.setViewId(viewId);
      redirect.execute();
      FacesMessages.afterPhase();
      AbstractSeamPhaseListener.storeAnyConversationContext(facesContext);
   }
   
   private void render(String viewId)
   {
      FacesContext context = FacesContext.getCurrentInstance();
      if ( !"".equals(viewId) )
      {
         if ( log.isDebugEnabled() ) log.debug("rendering: " + viewId);
         UIViewRoot viewRoot = context.getApplication().getViewHandler().createView(context, viewId);
         context.setViewRoot(viewRoot);
      }
      context.renderResponse();
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

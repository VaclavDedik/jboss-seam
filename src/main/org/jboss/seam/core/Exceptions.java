package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.HttpError;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Redirect;
import org.jboss.seam.annotations.Render;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.interceptors.ExceptionInterceptor;
import org.jboss.seam.jsf.AbstractSeamPhaseListener;

/**
 * Holds metadata for pages defined in pages.xml, including
 * page actions and page descriptions.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.exceptions")
public class Exceptions 
{
   private static final Log log = LogFactory.getLog(ExceptionInterceptor.class);
   
   private List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>();
   
   public ExceptionHandler handle(Exception e) throws Exception
   {
      for (ExceptionHandler eh: exceptionHandlers)
      {
         if ( eh.isHandler(e) )
         {
            eh.handle(e);
            break;
         }
      }
      return null;
   }
   
   @Create
   public void init()
   {
      exceptionHandlers.add( new RenderHandler() );
      exceptionHandlers.add( new RedirectHandler() );
      exceptionHandlers.add( new ErrorHandler() );
      if ( Init.instance().isDebug() ) 
      {
         exceptionHandlers.add( new DebugPageHandler() );
      }
   }

   public static interface ExceptionHandler
   {
      public void handle(Exception e) throws Exception;
      public boolean isHandler(Exception e);
   }
   
   public static class RedirectHandler implements ExceptionHandler
   {
      public void handle(Exception e) throws Exception
      {
         addFacesMessage(e, getMessage(e));
         redirect( getViewId(e) );
         handled(e);
         throw e;
      }

      public boolean isHandler(Exception e)
      {
         return e.getClass().isAnnotationPresent(Redirect.class) && 
               Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE;
      }
      
      protected String getMessage(Exception e)
      {
         return e.getClass().getAnnotation(Redirect.class).message();
      }
      
      protected String getViewId(Exception e)
      {
         return e.getClass().getAnnotation(Redirect.class).viewId();
      }
      
   }
   
   public static class RenderHandler implements ExceptionHandler
   {
      public void handle(Exception e)
      {
         addFacesMessage(e, getMessage(e));
         render(getViewId(e));
      }

      public boolean isHandler(Exception e)
      {
         return  e.getClass().isAnnotationPresent(Render.class) && 
               Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION;
      }

      protected String getMessage(Exception e)
      {
         return e.getClass().getAnnotation(Redirect.class).message();
      }
      
      protected String getViewId(Exception e)
      {
         return e.getClass().getAnnotation(Redirect.class).viewId();
      }
   }
   
   public static class ErrorHandler implements ExceptionHandler
   {
      public void handle(Exception e) throws Exception
      {
         String message = getMessage(e); 
         message = message==null ? 
               null : Interpolator.instance().interpolate(message);
         error(getCode(e), message);
         handled(e);
      }  

      public boolean isHandler(Exception e)
      {
         return e.getClass().isAnnotationPresent(HttpError.class);
      }
      
      protected String getMessage(Exception e)
      {
         String message = e.getClass().getAnnotation(HttpError.class).message();
         if ( "".equals(message) ) message = e.getMessage();
         return message;
      }
      
      protected int getCode(Exception e)
      {
         return e.getClass().getAnnotation(HttpError.class).errorCode();
      }
      
   }
   
   public static class DebugPageHandler implements ExceptionHandler
   {

      public void handle(Exception e)
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
         handled(e);
      }

      public boolean isHandler(Exception e)
      {
         return Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE;
      }
      
   }
   
   protected static void addFacesMessage(Exception e, String message)
   {
      String message1 = message;
      message1 = "".equals(message1) ? e.getMessage() : message1;
      message = message1==null ? null : Interpolator.instance().interpolate(message1);
      if (message!=null)
      {
         FacesMessages.instance().add(message);
      }
   }
   
   protected static void error(int code, String message)
   {
      if ( log.isDebugEnabled() ) log.debug("sending error: " + code);
      FacesContext facesContext = FacesContext.getCurrentInstance();
      org.jboss.seam.core.HttpError httpError = org.jboss.seam.core.HttpError.instance();
      if (message==null)
      {
         httpError.send(code);
      }
      else
      {
         httpError.send(code, message);
      }
      FacesMessages.afterPhase();
      AbstractSeamPhaseListener.storeAnyConversationContext(facesContext);
   }

   protected static void redirect(String viewId)
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
   
   protected static void render(String viewId)
   {
      FacesContext context = FacesContext.getCurrentInstance();
      if ( !"".equals(viewId) )
      {
         if ( log.isDebugEnabled() ) log.debug("rendering: " + viewId);
         UIViewRoot viewRoot = context.getApplication().getViewHandler()
               .createView(context, viewId);
         context.setViewRoot(viewRoot);
      }
      context.renderResponse();
   }

   protected static void handled(Exception e)
   {
      FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("org.jboss.seam.exceptionHandled", e);
   }

   public static Exceptions instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Exceptions) Component.getInstance(Exceptions.class, ScopeType.APPLICATION, true);
   }


}

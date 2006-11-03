package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;

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
   
   public Object handle(Exception e) throws Exception
   {
      for (ExceptionHandler eh: exceptionHandlers)
      {
         if ( eh.isHandler(e) )
         {
            return eh.handle(e);
         }
      }
      throw e;
   }
   
   @Create
   public void initialize() throws Exception 
   {
      InputStream stream = Resources.getResourceAsStream("/WEB-INF/exceptions.xml");
      ExceptionHandler anyhandler = null;
      if (stream==null)
      {
         log.info("no exceptions.xml file found");
      }
      else
      {
         log.info("reading exceptions.xml");
         SAXReader saxReader = new SAXReader();
         saxReader.setMergeAdjacentText(true);
         Document doc = saxReader.read(stream);
         List<Element> elements = doc.getRootElement().elements("exception");
         for (Element exception: elements)
         {
            String className = exception.attributeValue("class");
            if (className==null)
            {
               anyhandler = createHandler(exception, Exception.class);
            }
            else
            {
               ExceptionHandler handler = createHandler( exception, Reflections.classForName(className) );
               if (handler!=null) exceptionHandlers.add(handler);
            }
         }
      }
      
      exceptionHandlers.add( new RenderHandler() );
      exceptionHandlers.add( new RedirectHandler() );
      exceptionHandlers.add( new ErrorHandler() );
      if ( Init.instance().isDebug() ) 
      {
         exceptionHandlers.add( new DebugPageHandler() );
      }
      
      if (anyhandler!=null) exceptionHandlers.add(anyhandler);
   }

   private ExceptionHandler createHandler(Element exception, final Class clazz)
   {
      final boolean endConversation = exception.elementIterator("end-conversation").hasNext();

      Element render = exception.element("render");
      if (render!=null)
      {
         final String viewId = render.attributeValue("view-id");
         final String message = render.getTextTrim();
         return new RenderHandler()
         {
            @Override
            protected String getMessage(Exception e)
            {
               return message;
            }
            @Override
            protected String getViewId(Exception e)
            {
               return viewId;
            }
            @Override
            public boolean isHandler(Exception e)
            {
               return clazz.isInstance(e) && 
                     Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION;
            }
            @Override
            protected boolean isEnd(Exception e)
            {
               return endConversation;
            }
         };
      }
      
      Element redirect = exception.element("redirect");
      if (redirect!=null)
      {
         final String viewId = redirect.attributeValue("view-id");
         final String message = redirect.getTextTrim();
         return new RedirectHandler()
         {
            @Override
            protected String getMessage(Exception e)
            {
               return message;
            }
            @Override
            protected String getViewId(Exception e)
            {
               return viewId;
            }
            @Override
            public boolean isHandler(Exception e)
            {
               return clazz.isInstance(e) && 
                     Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE;
            }
            @Override
            protected boolean isEnd(Exception e)
            {
               return endConversation;
            }
         };
      }
      
      Element error = exception.element("http-error");
      if (error!=null)
      {
         final int code = Integer.parseInt( error.attributeValue("view-id") );
         final String message = error.getTextTrim();
         return new ErrorHandler()
         {
            @Override
            protected String getMessage(Exception e)
            {
               return message;
            }
            @Override
            protected int getCode(Exception e)
            {
               return code;
            }
            @Override
            public boolean isHandler(Exception e)
            {
               return clazz.isInstance(e);
            }
            @Override
            protected boolean isEnd(Exception e)
            {
               return endConversation;
            }
         };
      }
      
      return null;
   }

   public static interface ExceptionHandler
   {
      public Object handle(Exception e) throws Exception;
      public boolean isHandler(Exception e);
   }
   
   public static class RedirectHandler implements ExceptionHandler
   {
      public Object handle(Exception e) throws Exception
      {
         addFacesMessage( e, getMessage(e) );
         if ( isEnd(e) ) Conversation.instance().end();
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
      
      protected boolean isEnd(Exception e)
      {
         return e.getClass().getAnnotation(Redirect.class).end();
      } 
   }
   
   public static class RenderHandler implements ExceptionHandler
   {
      public Object handle(Exception e)
      {
         addFacesMessage( e, getMessage(e) );
         if ( isEnd(e) ) Conversation.instance().end();
         render( getViewId(e) );
         return null;
      }

      public boolean isHandler(Exception e)
      {
         return e.getClass().isAnnotationPresent(Render.class) && 
               Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION;
      }

      protected String getMessage(Exception e)
      {
         return e.getClass().getAnnotation(Render.class).message();
      }
      
      protected String getViewId(Exception e)
      {
         return e.getClass().getAnnotation(Render.class).viewId();
      }

      protected boolean isEnd(Exception e)
      {
         return e.getClass().getAnnotation(Render.class).end();
      }
   }
   
   public static class ErrorHandler implements ExceptionHandler
   {
      public Object handle(Exception e) throws Exception
      {
         if ( isEnd(e) ) Conversation.instance().end();
         String message = getMessage(e);
         addFacesMessage(e, message);
         error( getCode(e), Interpolator.instance().interpolate( getDisplayMessage(e, message) ) );
         handled(e);
         throw e;
      }  

      public boolean isHandler(Exception e)
      {
         return e.getClass().isAnnotationPresent(HttpError.class);
      }
      
      protected String getMessage(Exception e)
      {
         return e.getClass().getAnnotation(HttpError.class).message();
      }
      
      protected int getCode(Exception e)
      {
         return e.getClass().getAnnotation(HttpError.class).errorCode();
      }
      
      protected boolean isEnd(Exception e)
      {
         return e.getClass().getAnnotation(HttpError.class).end();
      }      
   }
   
   public static class DebugPageHandler implements ExceptionHandler
   {

      public Object handle(Exception e) throws Exception
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
         throw e;
      }

      public boolean isHandler(Exception e)
      {
         return Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE;
      }
      
   }
   
   protected static String getDisplayMessage(Exception e, String message)
   {
      if ( Strings.isEmpty(message) && e.getMessage()!=null ) 
      {
         return e.getMessage();
      }
      else
      {
         return message;
      }
   }
   
   protected static void addFacesMessage(Exception e, String message)
   {
      message = getDisplayMessage(e, message);
      if ( !Strings.isEmpty(message) )
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
      if ( Strings.isEmpty(viewId) )
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

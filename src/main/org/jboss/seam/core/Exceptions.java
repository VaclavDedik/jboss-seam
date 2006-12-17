package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.HttpError;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Redirect;
import org.jboss.seam.annotations.Render;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;
import org.jboss.seam.util.Transactions;
import org.jboss.seam.util.XML;

/**
 * Holds metadata for pages defined in pages.xml, including
 * page actions and page descriptions.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Install(precedence=BUILT_IN)
@Name("org.jboss.seam.core.exceptions")
public class Exceptions extends Navigator
{
   private static final LogProvider log = Logging.getLogProvider(Exceptions.class);
   
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
         List<Element> elements = XML.getRootElement(stream).elements("exception");
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
      final boolean rollback = exception.elementIterator("rollback").hasNext();

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
            @Override
            protected boolean isRollback(Exception e)
            {
               return rollback;
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
            @Override
            protected boolean isRollback(Exception e)
            {
               return rollback;
            }
         };
      }
      
      Element error = exception.element("http-error");
      if (error!=null)
      {
         String errorCode = error.attributeValue("error-code");
         final int code = Strings.isEmpty(errorCode) ? 
               500 : Integer.parseInt(errorCode);
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
            @Override
            protected boolean isRollback(Exception e)
            {
               return rollback;
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
         if ( isRollback(e) ) Transactions.setTransactionRollbackOnly();
         redirect( getViewId(e), null );
         return rethrow(e);
      }

      public boolean isHandler(Exception e)
      {
         return e.getClass().isAnnotationPresent(Redirect.class) && 
               Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE && 
               Lifecycle.getPhaseId()!=null;
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

      protected boolean isRollback(Exception e)
      {
         return e.getClass().getAnnotation(Redirect.class).rollback();
      }
      
      @Override
      public String toString()
      {
         return "RedirectHandler";
      }
   }
   
   public static class RenderHandler implements ExceptionHandler
   {
      public Object handle(Exception e) throws Exception
      {
         addFacesMessage( e, getMessage(e) );
         if ( isEnd(e) ) Conversation.instance().end();
         if ( isRollback(e) ) Transactions.setTransactionRollbackOnly();
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

      protected boolean isRollback(Exception e)
      {
         return e.getClass().getAnnotation(Render.class).rollback();
      }
      
      @Override
      public String toString()
      {
         return "RenderHandler";
      }
   }
   
   public static class ErrorHandler implements ExceptionHandler
   {
      public Object handle(Exception e) throws Exception
      {
         if ( isEnd(e) ) Conversation.instance().end();
         if ( isRollback(e) ) Transactions.setTransactionRollbackOnly();
         String message = getMessage(e);
         //addFacesMessage(e, message);
         error( getCode(e), Interpolator.instance().interpolate( getDisplayMessage(e, message) ) );
         return rethrow(e);
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
      
      protected boolean isRollback(Exception e)
      {
         return e.getClass().getAnnotation(HttpError.class).rollback();
      }
      
      @Override
      public String toString()
      {
         return "ErrorHandler";
      }
   }
   
   public static class DebugPageHandler implements ExceptionHandler
   {

      public Object handle(Exception e) throws Exception
      {
         log.error("redirecting to debug page", e);
         Context conversationContext = Contexts.getConversationContext();
         conversationContext.set("org.jboss.seam.debug.lastException", e);
         conversationContext.set("org.jboss.seam.debug.phaseId", Lifecycle.getPhaseId().toString());
         org.jboss.seam.core.Redirect redirect = org.jboss.seam.core.Redirect.instance();
         redirect.setViewId("/debug.xhtml");
         Manager manager = Manager.instance();
         manager.beforeRedirect();
         redirect.setParameter( manager.getConversationIdParameter(), manager.getCurrentConversationId() );
         redirect.execute();
         conversationContext.flush();
         return rethrow(e);
      }

      public boolean isHandler(Exception e)
      {
         return Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE && 
               Lifecycle.getPhaseId()!=null;
      }
      
      @Override
      public String toString()
      {
         return "Debug";
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
   
   private static Object rethrow(Exception e) throws Exception
   {
      //SeamExceptionFilter does *not* do these things, which 
      //would normally happen in the phase listener after a 
      //responseComplete() call, but because we are rethrowing,
      //the phase listener might not get called (due to a bug!)
      /*FacesMessages.afterPhase();
      if ( Contexts.isConversationContextActive() )
      {
         Manager.instance().endRequest( ContextAdaptor.getSession( externalContext, true ) );
      }*/
      
      FacesContext facesContext = FacesContext.getCurrentInstance();
      facesContext.responseComplete();
      facesContext.getExternalContext().getRequestMap().put("org.jboss.seam.exceptionHandled", e);
      throw e;
   }

   public static Exceptions instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Exceptions) Component.getInstance(Exceptions.class, ScopeType.APPLICATION);
   }


}

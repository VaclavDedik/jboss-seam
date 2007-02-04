package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.exceptions.AnnotationErrorHandler;
import org.jboss.seam.exceptions.AnnotationRedirectHandler;
import org.jboss.seam.exceptions.ConfigErrorHandler;
import org.jboss.seam.exceptions.ConfigRedirectHandler;
import org.jboss.seam.exceptions.DebugPageHandler;
import org.jboss.seam.exceptions.ExceptionHandler;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.EJB;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Strings;
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
public class Exceptions
{
   private static final LogProvider log = Logging.getLogProvider(Exceptions.class);
   
   private List<ExceptionHandler> exceptionHandlers = new ArrayList<ExceptionHandler>();
   
   public void handle(Exception e) throws Exception
   {
      //build a list of the nested exceptions
      List<Exception> causes = new ArrayList<Exception>();
      for (Exception cause=e; cause!=null; cause=EJB.getCause(cause))
      {
         causes.add(cause);
      }
      //try to match each handler in turn
      for (ExceptionHandler eh: exceptionHandlers)
      {
         //Try to handle most-nested exception before least-nested
         for (int i=causes.size()-1; i>=0; i--)
         {
            Exception cause = causes.get(i);
            if ( eh.isHandler(cause) )
            {
               eh.handle(cause);
               return;
            }
         }
      }
      
      //finally, rethrow it, since no handler was found
      throw e;
   }
   
   @Create
   public void initialize() throws Exception 
   {
      ExceptionHandler anyhandler1 = parse("/WEB-INF/exceptions.xml"); //deprecated
      ExceptionHandler anyhandler2 = parse("/WEB-INF/pages.xml");
      
      exceptionHandlers.add( new AnnotationRedirectHandler() );
      exceptionHandlers.add( new AnnotationErrorHandler() );
      
      if ( Init.instance().isDebug() ) 
      {
         exceptionHandlers.add( new DebugPageHandler() );
      }
      
      if (anyhandler1!=null) exceptionHandlers.add(anyhandler1);
      if (anyhandler2!=null) exceptionHandlers.add(anyhandler2);
   }

   private ExceptionHandler parse(String fileName) throws DocumentException, ClassNotFoundException
   {
      ExceptionHandler anyhandler = null;
      InputStream stream = Resources.getResourceAsStream(fileName);
      if (stream!=null)
      {
         log.info("reading exception mappings from " + fileName);
         List<Element> elements = XML.getRootElement(stream).elements("exception");
         for (final Element exception: elements)
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
      return anyhandler;
   }

   private ExceptionHandler createHandler(Element exception, final Class clazz)
   {
      final boolean endConversation = exception.elementIterator("end-conversation").hasNext();
      final boolean rollback = exception.elementIterator("rollback").hasNext();
      
      Element redirect = exception.element("redirect");
      if (redirect!=null)
      {
         final String viewId = redirect.attributeValue("view-id");
         Element facesMessage = redirect.element("faces-message");
         final String message = facesMessage==null ? null : facesMessage.getTextTrim();
         return new ConfigRedirectHandler(viewId, clazz, endConversation, rollback, message);
      }
      
      Element error = exception.element("http-error");
      if (error!=null)
      {
         String errorCode = error.attributeValue("error-code");
         final int code = Strings.isEmpty(errorCode) ? 
               500 : Integer.parseInt(errorCode);
         final String message = error.getTextTrim();
         return new ConfigErrorHandler(message, endConversation, clazz, code, rollback);
      }
      
      return null;
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

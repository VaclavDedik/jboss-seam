/*
  * JBoss, Home of Professional Open Source
  *
  * Distributable under LGPL license.
  * See terms of license at gnu.org.
  */
package org.jboss.seam;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.ejb.Remove;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.BeginConversation;
import org.jboss.seam.annotations.BeginConversationIf;
import org.jboss.seam.annotations.EndConversation;
import org.jboss.seam.annotations.EndConversationIf;

/**
 * Interceptor for injection and conversation scope management
 * 
 * @author Gavin King
 * @version $Revision$
 */
public class SeamInterceptor
{

   private static final Logger log = Logger.getLogger(SeamInterceptor.class);

   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Object bean = invocation.getBean();
      String name = Seam.getComponentName(bean.getClass());
      SeamComponent seamComponent = new SeamVariableResolver().findSeamComponent(name);

      final Method method = invocation.getMethod();
      boolean begun = beginConversation(method);

      if ( seamComponent.getInjectFields().size()>0 || seamComponent.getInjectMethods().size()>0 ) //only needed to hush the log message
      {
         log.info("injecting dependencies to: " + name);
         seamComponent.inject(bean);
      }

      Object result;
      try
      {
         result = invocation.proceed();
      } 
      catch (Exception exception)
      {
         if (begun)
         {
            abortBeginConversation();
         }
         endConversation(method, exception);
         removeIfNecessary(bean, method, true, seamComponent);
         throw exception;
      }
      
      if (begun) 
      {
         abortBeginConversation(method, result);  
      }
      endConversation(method, result);
      removeIfNecessary(bean, method, false, seamComponent);
      return result;
   }
   
   /**
    * If it was a @Remove method, also remove the component instance from the context
    */
   private void removeIfNecessary(Object bean, Method method, boolean exception, SeamComponent seamComponent)
   {
      boolean wasRemoved = method.isAnnotationPresent(Remove.class) &&
            ( !exception || !method.getAnnotation(Remove.class).retainIfException() );
      if ( wasRemoved )
      {
         seamComponent.getScope().getContext().remove( seamComponent.getName() );
         log.info("Stateful component was removed");
      }
   }

   /**
    * If we tried to begin a conversation, but an exception occurred, don't
    * begin after all
    */
   private void abortBeginConversation(Method method, Object result)
   {
      if ( method.isAnnotationPresent(BeginConversationIf.class) )
      {
         String[] results = method.getAnnotation(BeginConversationIf.class)
               .result();
         if (!Arrays.asList(results).contains(result))
         {
            Contexts.endConversation();
         }
      }
   }

   /**
    * If we tried to begin a conversation, but an exception occurred, don't
    * begin after all
    */
   private void abortBeginConversation()
   {
      Contexts.endConversation();
   }

   /**
    * If the method is annotated @BeginConversation, 
    * assign a new conversationId
    */
   private boolean beginConversation(Method method)
   {
      boolean beginConversation = method.isAnnotationPresent(BeginConversation.class) || 
            method.isAnnotationPresent(BeginConversationIf.class);
      if (beginConversation)
      {
         Contexts.beginConversation();
         return true;
      }
      return false;
   }

   /**
    * If the method is annotated @EndConversation and an exception 
    * occurred, end the conversation and clean up
    */
   private void endConversation(Method method, Exception exception)
   {
      if (method.isAnnotationPresent(EndConversationIf.class))
      {
         Class[] results = method.getAnnotation(EndConversationIf.class)
               .exception();
         if (Arrays.asList(results).contains(exception.getClass()))
         {
            Contexts.endConversation();
         }
      }
   }

   /**
    * If the method is annotated @EndConversation end the conversation and 
    * clean up
    */
   private void endConversation(Method method, Object result)
   {
      if (method.isAnnotationPresent(EndConversation.class))
      {
         Contexts.endConversation();
      }
      if (method.isAnnotationPresent(EndConversationIf.class))
      {
         String[] results = method.getAnnotation(EndConversationIf.class)
               .result();
         if (Arrays.asList(results).contains(result))
         {
            Contexts.endConversation();
         }
      }
   }

}

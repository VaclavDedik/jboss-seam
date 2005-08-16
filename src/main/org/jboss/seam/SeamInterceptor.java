/*
  * JBoss, Home of Professional Open Source
  *
  * Distributable under LGPL license.
  * See terms of license at gnu.org.
  */
package org.jboss.seam;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.ejb.Remove;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.BeginConversation;
import org.jboss.seam.annotations.BeginConversationIf;
import org.jboss.seam.annotations.EndConversation;
import org.jboss.seam.annotations.EndConversationIf;
import org.jboss.seam.annotations.Inject;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

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

      final Method method = invocation.getMethod();
      boolean begun = beginConversation(method);

      log.info("injecting dependencies to: " + bean.getClass().getName());
      injectFields(bean);
      injectMethods(bean);

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
         removeIfNecessary(bean, method, true);
         throw exception;
      }
      
      if (begun) 
      {
         abortBeginConversation(method, result);  
      }
      endConversation(method, result);
      removeIfNecessary(bean, method, false);
      return result;
   }
   
   /**
    * If it was a @Remove method, also remove the component instance from the context
    */
   private void removeIfNecessary(Object bean, Method method, boolean exception)
   {
      boolean wasRemoved = method.isAnnotationPresent(Remove.class) &&
            ( !exception || !method.getAnnotation(Remove.class).retainIfException() );
      if ( wasRemoved )
      {
         log.info("removing destroyed component");
         Class beanClass = bean.getClass();
         Seam.getComponentScope( beanClass ).getContext()
               .remove( Seam.getComponentName( beanClass ) );
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

   private void injectMethods(Object bean)
   {
      Method[] methods = bean.getClass().getDeclaredMethods();
      for (Method method : methods)
      {
         Inject inject = method.getAnnotation(Inject.class);
         if (inject != null)
         {
            if ( method.getReturnType()==Properties.class) 
            {
               injectProperties(bean, method, inject);
            }
            else if ( method.getReturnType()==ProcessInstance.class)
            {
               injectProcessInstance(bean, method, inject);
            }
            else 
            {
               injectComponent(bean, method, inject);
            }
         }
      }
   }

   private void injectFields(Object bean)
   {
      Field[] fields = bean.getClass().getDeclaredFields();
      for (Field field : fields)
      {
         Inject inject = field.getAnnotation(Inject.class);
         if (inject != null)
         {
            if ( field.getType()==Properties.class) 
            {
               injectProperties(bean, field, inject);
            }
            else if ( field.getType()==ProcessInstance.class)
            {
               injectProcessInstance(bean, field, inject);
            }
            else 
            {
               injectComponent(bean, field, inject);
            }
          }
      }
   }

   private void injectProperties(Object bean, Method method, Inject inject)
   {
      String resource = toName( method, inject.value(), ".properties" );
      inject(bean, method, resource, getProperties(bean, resource));
   }

   private void injectProperties(Object bean, Field field, Inject inject)
   {
      String resource = toName( field, inject.value(), ".properties" );
      inject(bean, field, resource, getProperties(bean, resource));
   }

   private Properties getProperties(Object bean, String resource)
   {
      Properties props = new Properties();
      try
      {
         props.load(bean.getClass().getResourceAsStream(resource));
      } 
      catch (IOException ioe)
      {
         throw new RuntimeException(ioe);
      }
      return props;
   }

   private void injectComponent(Object bean, Method method, Inject inject)
   {
      String name = toName(method, inject.value(), "");
      Object value = new SeamVariableResolver()
            .resolveVariable(name, inject.create());
      inject(bean, method, name, value);
   }

   private void injectComponent(Object bean, Field field, Inject inject)
   {
      String name = toName(field, inject.value(), "");
      Object value = new SeamVariableResolver()
            .resolveVariable(name, inject.create());
      inject(bean, field, name, value);
   }

   private String toName(Method method, String name, String extension)
   {
      if (name.length() == 0)
      {
         name = method.getName().substring(3, 4).toLowerCase()
               + method.getName().substring(4)
               + extension;
      }
      return name;
   }

   private String toName(Field field, String name, String extension)
   {
      if (name.length() == 0)
      {
         name = field.getName() + extension;
      }
      return name;
   }

   private void inject(Object bean, Method method, String name, Object value)
   {
      try
      {
         log.info("injecting: " + name);
         if (!method.isAccessible())
         {
            method.setAccessible(true);
         }
         method.invoke( bean, new Object[] { value } );
      } 
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not inject: " + name, e);
      }
   }

   private void inject(Object bean, Field field, String name, Object value)
   {
      try
      {
         log.info("injecting: " + name);
         if (!field.isAccessible()) 
         {
            field.setAccessible(true);
         }
         field.set(bean, value);
      } 
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not inject: " + name, e);
      }
   }

   private void injectProcessInstance(Object bean, Field field, Inject inject)
   {
      String name = toName( field, inject.value(), "");
      inject(bean, field, name, getProcessInstance(name));
   }

   private void injectProcessInstance(Object bean, Method method, Inject inject)
   {
      String name = toName( method, inject.value(), "");
      inject(bean, method, name, getProcessInstance(name));
   }

   private ProcessInstance getProcessInstance(String name)
   {
      JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory
            .buildJbpmSessionFactory();

      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      ProcessInstance processInstance = null;
      try
      {
         ProcessDefinition processDefinition = jbpmSession.getGraphSession()
               .findLatestProcessDefinition(name);
         if (processDefinition != null)
         {
            processInstance = new ProcessInstance(processDefinition);
            jbpmSession.getGraphSession().saveProcessInstance(processInstance);
         } 
         else
         {
            log.warn("ProcessDefinition: " + name + " could be found");
         }
      } 
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      return processInstance;
   }

}

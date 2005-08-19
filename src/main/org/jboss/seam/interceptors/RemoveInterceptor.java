//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.ejb.InvocationContext;
import javax.ejb.Remove;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.After;
import org.jboss.seam.annotations.Before;

public class RemoveInterceptor extends AbstractInterceptor
{
   
   private static final Logger log = Logger.getLogger(RemoveInterceptor.class);

   @Override
   @After(BijectionInterceptor.class) //is this really necessary?
   @Before(ConversationInterceptor.class)
   public Object afterReturn(Object result, InvocationContext invocation)
   {
      removeIfNecessary( invocation.getBean(), invocation.getMethod(), false );
      return result;
   }

   @Override
   public Exception afterException(Exception exception, InvocationContext invocation)
   {
      removeIfNecessary( invocation.getBean(), invocation.getMethod(), true );
      return exception;
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
         component.getScope().getContext().remove( component.getName() );
         log.info("Stateful component was removed");
      }
   }

 

}

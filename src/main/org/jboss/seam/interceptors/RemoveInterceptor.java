//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.ejb.InvocationContext;
import javax.ejb.Remove;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Within;

/**
 * Removes components from the Seam context after invocation
 * of an EJB @Remove method.
 * 
 * @author Gavin King
 */
@Around(BijectionInterceptor.class) //is this really necessary?
@Within(ConversationInterceptor.class)
public class RemoveInterceptor extends AbstractInterceptor
{
   
   private static final Logger log = Logger.getLogger(RemoveInterceptor.class);

   @Override
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

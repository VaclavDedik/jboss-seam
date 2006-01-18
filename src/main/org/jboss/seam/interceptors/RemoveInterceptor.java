//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.rmi.RemoteException;

import javax.ejb.ApplicationException;
import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.ejb.Remove;

import org.jboss.logging.Logger;
import org.jboss.seam.ComponentType;
import org.jboss.seam.annotations.Around;

/**
 * Removes components from the Seam context after invocation
 * of an EJB @Remove method.
 * 
 * @author Gavin King
 */
@Around({ValidationInterceptor.class, BijectionInterceptor.class, ConversationInterceptor.class})
public class RemoveInterceptor extends AbstractInterceptor
{
    
   //TODO: note that this implementation is a bit broken, since it assumes that
   //      the thing is always bound to its component name and scope
   
   private static final Logger log = Logger.getLogger(RemoveInterceptor.class);

   @AroundInvoke
   public Object removeIfNecessary(InvocationContext invocation) throws Exception
   {
      Object result;
      try
      {
         result = invocation.proceed();
      }
      catch (Exception exception)
      {
         removeIfNecessary( invocation.getMethod(), exception );
         throw exception;
      }

      removeIfNecessary( invocation.getMethod() );
      return result;
   }

   private void removeIfNecessary(Method method, Exception exception) {
      if ( exception instanceof RuntimeException || exception instanceof RemoteException )
      {
         if ( !exception.getClass().isAnnotationPresent(ApplicationException.class) ) 
         {
            //it is a "system exception"
            if ( component.getType()==ComponentType.STATEFUL_SESSION_BEAN )
            {
               remove();
            }
         }
      }
      else if ( method.isAnnotationPresent(Remove.class) )
      {
         if ( !method.getAnnotation(Remove.class).retainIfException() ) 
         {
            remove();
         }
      }
   }

   private void removeIfNecessary(Method method)
   {
      if ( method.isAnnotationPresent(Remove.class) ) 
      {
         remove();
      }
   }

   private void remove() {
      component.getScope().getContext().remove( component.getName() );
      log.debug("Stateful component was removed: " + component.getName());
   }

}

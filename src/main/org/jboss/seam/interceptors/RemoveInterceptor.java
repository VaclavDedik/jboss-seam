//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.rmi.RemoteException;

import javax.ejb.ApplicationException;
import javax.ejb.Remove;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ComponentType;
import org.jboss.seam.annotations.Interceptor;

/**
 * Removes components from the Seam context after invocation
 * of an EJB @Remove method.
 * 
 * @author Gavin King
 */
@Interceptor(stateless=true,
             around={ValidationInterceptor.class, BijectionInterceptor.class, ConversationInterceptor.class})
public class RemoveInterceptor extends AbstractInterceptor
{
    
   //TODO: note that this implementation is a bit broken, since it assumes that
   //      the thing is always bound to its component name and scope
   //      (We are waiting for getInvokedBusinessObject() in EJB3)
   
   private static final Log log = LogFactory.getLog(RemoveInterceptor.class);

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
      //TODO: account for roles, by checking which role the component
      //      is actually bound to (need getInvokedBusinessObject())
      component.getScope().getContext().remove( component.getName() );
      log.debug("Stateful component was removed: " + component.getName());
   }

}

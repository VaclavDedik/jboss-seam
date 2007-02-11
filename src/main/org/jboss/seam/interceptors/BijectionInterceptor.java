//$Id$
package org.jboss.seam.interceptors;



import org.jboss.seam.Component;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Before invoking the component, inject all dependencies. After
 * invoking, outject dependencies back into their context.
 * 
 * @author Gavin King
 */
@Interceptor
public class BijectionInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = 4686458105931528659L;
   
   private static final LogProvider log = Logging.getLogProvider(BijectionInterceptor.class);
   
   private boolean reentrant; //OK, since all Seam components are single-threaded
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      if (reentrant)
      {
         if ( log.isTraceEnabled() )
         {
            log.trace("reentrant call to component: " + getComponent().getName() );
         }
         return invocation.proceed();
      }
      else
      {
         reentrant = true;
         try
         {
            Component component = getComponent();
            boolean enforceRequired = !component.isLifecycleMethod( invocation.getMethod() );
            component.inject( invocation.getTarget(), enforceRequired );
            Object result = invocation.proceed();            
            component.outject( invocation.getTarget(), enforceRequired );
            component.disinject( invocation.getTarget() );
            return result;
            
         }
         finally
         {
            reentrant = false;
         }
      }
   }

}

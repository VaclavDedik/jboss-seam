//$Id$
package org.jboss.seam.interceptors;

import static org.jboss.seam.util.EJB.POST_ACTIVATE;
import static org.jboss.seam.util.EJB.POST_CONSTRUCT;
import static org.jboss.seam.util.EJB.PRE_DESTROY;
import static org.jboss.seam.util.EJB.PRE_PASSIVATE;

import java.lang.reflect.Method;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
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
   
   private static boolean isLifecycleMethod(Method method)
   {
      return method==null || //EJB 3 JavaDoc says InvocationContext.getMethod() returns null for lifecycle callbacks!
            method.isAnnotationPresent(Create.class) || method.isAnnotationPresent(Destroy.class) ||
            method.isAnnotationPresent(POST_CONSTRUCT) || method.isAnnotationPresent(PRE_DESTROY) ||
            method.isAnnotationPresent(PRE_PASSIVATE) || method.isAnnotationPresent(POST_ACTIVATE);
   }

   @AroundInvoke
   public Object bijectComponent(InvocationContext invocation) throws Exception
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
            return bijectNonreentrantComponent(invocation);
         }
         finally
         {
            reentrant = false;
         }
      }
   }
   
   private Object bijectNonreentrantComponent(InvocationContext invocation) throws Exception
   {
      
      if ( getComponent().needsInjection() ) //only needed to hush the log message
      {
         if ( log.isTraceEnabled() )
         {
            log.trace("injecting dependencies of: " + getComponent().getName());
         }
         getComponent().inject( invocation.getTarget(), !isLifecycleMethod( invocation.getMethod() ) );
      }
      
      Object result = invocation.proceed();
      
      if ( getComponent().needsOutjection() ) //only needed to hush the log message
      {
         if ( log.isTraceEnabled() )
         {
            log.trace("outjecting dependencies of: " + getComponent().getName());
         }
         getComponent().outject( invocation.getTarget(), !isLifecycleMethod( invocation.getMethod() ) );
      }
      
      if ( getComponent().needsInjection() ) //only needed to hush the log message
      {
         if ( log.isTraceEnabled() )
         {
            log.trace("disinjecting dependencies of: " + getComponent().getName());
         }
         getComponent().disinject( invocation.getTarget() );
      }
      
      //method parameter injection?
      /*Method method = invocation.getMethod();
      if (method!=null) //TODO: YEW! for unit tests
      {
         //TODO: could this be slow?
         Annotation[][] allParameterAnnotations = method.getParameterAnnotations();
         for( int i=0; i<allParameterAnnotations.length; i++ )
         {
            Annotation[] paramAnns = allParameterAnnotations[i];
            for ( Annotation ann: paramAnns )
            {
               if (ann instanceof In)
               {
                  String name = ( (In) ann ).value();
                  if (name==null)
                  {
                     throw new IllegalArgumentException("@RequestIn must specify a parameter name when used in a method parameter");
                  }
                  invocation.getParameters()[i] = Component.getInstance(name);
               }
               else if (ann instanceof RequestParameter)
               {
                  String name = ( (RequestParameter) ann ).value();
                  if (name==null)
                  {
                     throw new IllegalArgumentException("@RequestParameter must specify a parameter name when used in a method parameter");
                  }
                  invocation.getParameters()[i] = Component.convertMultiValueRequestParameter( 
                        Component.getRequestParameters(), 
                        name, 
                        method.getParameterTypes()[i] 
                     );
               }
            }
         }
      }*/
      
      return result;
   }

}

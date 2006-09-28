//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;

/**
 * Before invoking the component, inject all dependencies. After
 * invoking, outject dependencies back into their context.
 * 
 * @author Gavin King
 */
public class BijectionInterceptor extends AbstractInterceptor
{
   
   private static final Log log = LogFactory.getLog(BijectionInterceptor.class);
   
   private static boolean isLifecycleMethod(Method method)
   {
      return method==null || //EJB 3 JavaDoc says InvocationContext.getMethod() returns null for lifecycle callbacks!
            method.isAnnotationPresent(Create.class) || method.isAnnotationPresent(Destroy.class) ||
            method.isAnnotationPresent(PostConstruct.class) || method.isAnnotationPresent(PreDestroy.class) ||
            method.isAnnotationPresent(PrePassivate.class) || method.isAnnotationPresent(PostActivate.class);
   }

   @AroundInvoke
   public Object bijectTargetComponent(InvocationContext invocation) throws Exception
   {
      if ( component.needsInjection() ) //only needed to hush the log message
      {
         if ( log.isTraceEnabled() )
         {
            log.trace("injecting dependencies of: " + component.getName());
         }
         component.inject( invocation.getTarget(), !isLifecycleMethod( invocation.getMethod() ) );
      }
      
      Object result = invocation.proceed();
      
      if ( component.needsOutjection() ) //only needed to hush the log message
      {
         if ( log.isTraceEnabled() )
         {
            log.trace("outjecting dependencies of: " + component.getName());
         }
         component.outject( invocation.getTarget(), isLifecycleMethod( invocation.getMethod() ) );
      }
      
      if ( component.needsInjection() ) //only needed to hush the log message
      {
         if ( log.isTraceEnabled() )
         {
            log.trace("disinjecting dependencies of: " + component.getName());
         }
         component.disinject( invocation.getTarget() );
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

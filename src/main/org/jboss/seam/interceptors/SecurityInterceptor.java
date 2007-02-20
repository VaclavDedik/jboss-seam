package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import org.jboss.seam.InterceptorType;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.security.Identity;
import org.jboss.seam.util.Strings;

/**
 * Provides authorization services for component invocations.
 * 
 * @author Shane Bryzak
 */
@Interceptor(stateless = true, type=InterceptorType.CLIENT, 
         around=AsynchronousInterceptor.class)
public class SecurityInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = -6567750187000766925L;

   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Method interfaceMethod = invocation.getMethod();
      //TODO: optimize this:
      Method method = getComponent().getBeanClass().getMethod( interfaceMethod.getName(), interfaceMethod.getParameterTypes() );
      Restrict restrict = getRestriction(method);
      if (restrict != null)
      {
         String expr = !Strings.isEmpty( restrict.value() ) ? 
                  restrict.value() : createDefaultExpr(method);
         Identity.instance().checkRestriction(expr);
      }
      return invocation.proceed();
   }

   private Restrict getRestriction(Method method)
   {
      if ( method.isAnnotationPresent(Restrict.class) )
      {
         return method.getAnnotation(Restrict.class);
      }
      else if ( getComponent().getBeanClass().isAnnotationPresent(Restrict.class) )
      {
         if ( !getComponent().isLifecycleMethod(method) )
         {
            return getComponent().getBeanClass().getAnnotation(Restrict.class);
         }
      }
      return null;
   }
   
   /**
    * Creates a default security expression for a specified method.  The method must
    * be a method of a Seam component.
    * 
    * @param method The method for which to create a default permission expression 
    * @return The generated security expression.
    */
   private String createDefaultExpr(Method method)
   {
      return String.format( "#{s:hasPermission('%s','%s', null)}", getComponent().getName(), method.getName() );
   }
}

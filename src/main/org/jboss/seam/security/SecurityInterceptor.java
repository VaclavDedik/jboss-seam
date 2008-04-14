package org.jboss.seam.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.annotations.intercept.InterceptorType;
import org.jboss.seam.annotations.security.PermissionCheck;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.async.AsynchronousInterceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.util.Strings;

/**
 * Provides authorization services for component invocations.
 * 
 * @author Shane Bryzak
 */
@Interceptor(type=InterceptorType.CLIENT, 
         around=AsynchronousInterceptor.class)
public class SecurityInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = -6567750187000766925L;
   
   private Map<Method,Restriction> restrictions = new HashMap<Method,Restriction>();
   
   private class Restriction
   {
      private String expression;
      
      private Object target;
      private String action;
            
      public void setExpression(String expression)
      {
         this.expression = expression;
      }
      
      public void setTarget(Object target)
      {
         this.target = target;
      }
      
      public void setAction(String action)
      {
         this.action = action;
      }
      
      public void check()
      {
         if (Identity.isSecurityEnabled())
         {
            if (expression != null)
            {
               Identity.instance().checkRestriction(expression);
            }
            else if (target != null && action != null)
            {
               Identity.instance().checkPermission(target, action);
            }
         }
      }
   }

   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Method interfaceMethod = invocation.getMethod();
      
      Restriction restriction = getRestriction(interfaceMethod);
      
      if ( restriction != null ) restriction.check();

      return invocation.proceed();
   }

   private Restriction getRestriction(Method interfaceMethod) throws Exception
   {
      if (!restrictions.containsKey(interfaceMethod))
      {
         synchronized(restrictions)
         {
            if (!restrictions.containsKey(interfaceMethod))
            {               
               Method method = getComponent().getBeanClass().getMethod( 
                     interfaceMethod.getName(), interfaceMethod.getParameterTypes() );      
               
               Restrict restrict = null;
               
               if ( method.isAnnotationPresent(Restrict.class) )
               {
                  restrict = method.getAnnotation(Restrict.class);
               }
               else if ( getComponent().getBeanClass().isAnnotationPresent(Restrict.class) )
               {
                  if ( !getComponent().isLifecycleMethod(method) )
                  {
                     restrict = getComponent().getBeanClass().getAnnotation(Restrict.class); 
                  }
               }
               
               if (restrict != null)
               {
                  Restriction restriction = new Restriction();
                  restriction.setExpression(!Strings.isEmpty( restrict.value() ) ? 
                        restrict.value() : createDefaultExpr(method));
                  restrictions.put(interfaceMethod, restriction);
                  return restriction;
               }
               
               for (Annotation annotation : method.getAnnotations())
               {
                  if (annotation.annotationType().isAnnotationPresent(PermissionCheck.class))
                  {
                     PermissionCheck permissionAction = annotation.annotationType().getAnnotation(PermissionCheck.class);
                     
                     Method valueMethod = null;
                     for (Method m : annotation.annotationType().getDeclaredMethods())
                     {
                        valueMethod = m;
                        break;
                     }
                     
                     if (valueMethod != null)
                     {
                        Restriction restriction = new Restriction();
                        restriction.setTarget(valueMethod.invoke(annotation));
                        
                        if (!"".equals(permissionAction.value()))
                        {
                           restriction.setAction(permissionAction.value());
                        }
                        else
                        {
                           // If the PermissionAction.value isn't set, just use the lower-case version of the annotation name
                           restriction.setAction(annotation.annotationType().getSimpleName().toLowerCase());
                        }
                        restrictions.put(interfaceMethod, restriction);
                        return restriction;
                     }
                  }
               }
               
               restrictions.put(interfaceMethod, null);
               return null;
            }
         }
      }
      return restrictions.get(interfaceMethod);      
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
      return String.format( "#{s:hasPermission('%s','%s')}", 
            getComponent().getName(), method.getName() );
   }
}

package org.jboss.seam.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.annotations.intercept.InterceptorType;
import org.jboss.seam.annotations.security.PermissionCheck;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.security.RoleCheck;
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
      
      private Map<String, Object> methodRestrictions;
      private Map<Integer,Set<String>> paramRestrictions;
      private Set<String> roleRestrictions;
            
      public void setExpression(String expression)
      {
         this.expression = expression;
      }
      
      public void addMethodRestriction(Object target, String action)
      {
         if (methodRestrictions == null)
         {
            methodRestrictions = new HashMap<String, Object>();
         }
         
         methodRestrictions.put(action, target);
      }
      
      public void addRoleRestriction(String role)
      {
         if (roleRestrictions == null)
         {
            roleRestrictions = new HashSet<String>();
         }
         
         roleRestrictions.add(role);
      }
      
      public void addParameterRestriction(int index, String action)
      {
         Set<String> actions = null;
         
         if (paramRestrictions == null)
         {
            paramRestrictions = new HashMap<Integer,Set<String>>();
         }
         
         if (!paramRestrictions.containsKey(index))
         {
            actions = new HashSet<String>();
            paramRestrictions.put(index, actions);
         }
         else
         {
            actions = paramRestrictions.get(index);
         }
         
         actions.add(action);
      }
      
      public void check(Object[] parameters)
      {
         if (Identity.isSecurityEnabled())
         {
            if (expression != null)
            {
               Identity.instance().checkRestriction(expression);
            }
            
            if (methodRestrictions != null)
            {
               for (String action : methodRestrictions.keySet())
               {
                  Identity.instance().checkPermission(methodRestrictions.get(action), action);
               }
            }
            
            if (paramRestrictions != null)
            {
               for (Integer idx : paramRestrictions.keySet())
               {
                  Set<String> actions = paramRestrictions.get(idx);
                  for (String action : actions) 
                  {
                     Identity.instance().checkPermission(parameters[idx], action);
                  }
               }
            }
            
            if (roleRestrictions != null)
            {
               for (String role : roleRestrictions)
               {
                  Identity.instance().checkRole(role);
               }
            }
         }
      }
   }

   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Method interfaceMethod = invocation.getMethod();
      
      Restriction restriction = getRestriction(interfaceMethod);
      
      if ( restriction != null ) restriction.check(invocation.getParameters());

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
               Restriction restriction = null;
               
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
                  if (restriction == null) restriction = new Restriction();
                  restriction.setExpression(!Strings.isEmpty( restrict.value() ) ? 
                        restrict.value() : createDefaultExpr(method));
               }
               
               for (Annotation annotation : method.getDeclaringClass().getAnnotations())
               {
                  if (annotation.annotationType().isAnnotationPresent(RoleCheck.class))
                  {
                     if (restriction == null) restriction = new Restriction();
                     restriction.addRoleRestriction(annotation.annotationType().getSimpleName().toLowerCase());
                  }
               }
               
               for (Annotation annotation : method.getAnnotations())
               {
                  if (annotation.annotationType().isAnnotationPresent(PermissionCheck.class))
                  {
                     PermissionCheck permissionCheck = annotation.annotationType().getAnnotation(
                           PermissionCheck.class);
                     
                     Method valueMethod = null;
                     for (Method m : annotation.annotationType().getDeclaredMethods())
                     {
                        valueMethod = m;
                        break;
                     }
                     
                     if (valueMethod != null)
                     {                        
                        if (restriction == null) restriction = new Restriction();
                        Object target = valueMethod.invoke(annotation);
                        if (!target.equals(void.class))
                        {
                           if (restriction == null) restriction = new Restriction();
                           restriction.addMethodRestriction(target, 
                                 getPermissionAction(permissionCheck, annotation));
                        }
                     }
                  }
                  if (annotation.annotationType().isAnnotationPresent(RoleCheck.class))
                  {
                     if (restriction == null) restriction = new Restriction();
                     restriction.addRoleRestriction(annotation.annotationType().getSimpleName().toLowerCase());
                  }
               }               
               
               for (int i = 0; i < method.getParameterAnnotations().length; i++)
               {
                  Annotation[] annotations = method.getParameterAnnotations()[i]; 
                  for (Annotation annotation : annotations)
                  {
                     if (annotation.annotationType().isAnnotationPresent(PermissionCheck.class))
                     {                        
                        PermissionCheck permissionCheck = annotation.annotationType().getAnnotation(
                              PermissionCheck.class);
                        if (restriction == null) restriction = new Restriction();
                        restriction.addParameterRestriction(i, 
                              getPermissionAction(permissionCheck, annotation));                        
                     }
                  }
               }                             
               
               restrictions.put(interfaceMethod, restriction);
               return restriction;
            }
         }
      }
      return restrictions.get(interfaceMethod);      
   }
   
   private String getPermissionAction(PermissionCheck check, Annotation annotation)
   {
      if (!"".equals(check.value()))
      {
         return check.value();
      }
      else
      {
         return annotation.annotationType().getSimpleName().toLowerCase();
      }
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

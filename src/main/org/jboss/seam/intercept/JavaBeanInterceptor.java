//$Id$
package org.jboss.seam.intercept;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;

/**
 * Controller interceptor for JavaBean components
 * 
 * @author Gavin King
 */
public class JavaBeanInterceptor extends RootInterceptor
      implements MethodInterceptor, Serializable
{
   
   private boolean recursive = false;
   
   public JavaBeanInterceptor(Component component)
   {
      super(InterceptorType.ANY);
      init(component);
   }

   public Object intercept(final Object target, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {
      if (recursive) 
      {
         return methodProxy.invokeSuper(target, params);
      }
      
      recursive = true;
      try
      {
         String methodName = method.getName();
         if ( "finalize".equals(methodName) ) 
         {
            return methodProxy.invokeSuper(target, params);
         }
         else if ( "sessionDidActivate".equals(methodName) )
         {
            callPostActivate(target);
            return null;
         }
         else if ( "sessionWillPassivate".equals(methodName) )
         {
            callPrePassivate(target);
            return null;
         }
         else
         {
            return interceptInvocation(target, method, params, methodProxy);
         }
      }
      finally
      {
         recursive = false;
      }
   }

   private void callPrePassivate(final Object target)
   {
      prePassivate( new RootInvocationContext(target, getComponent().getPrePassivateMethod(), new Object[0])
      {
         public Object proceed() throws Exception
         {
            getComponent().callPrePassivateMethod(target);
            return null;
         }
         
      } );
   }

   private void callPostActivate( final Object target)
   {
      postActivate( new RootInvocationContext(target, getComponent().getPostActivateMethod(), new Object[0])
      {
         public Object proceed() throws Exception
         {
            getComponent().callPostActivateMethod(target);
            return null;
         }
         
      } );
   }

   private Object interceptInvocation(final Object target, final Method method, final Object[] params, 
         final MethodProxy methodProxy) throws Exception
   {
      return aroundInvoke( new RootInvocationContext(target, method, params)
      {
         
         public Object proceed() throws Exception
         {
            try
            {
               return methodProxy.invokeSuper(target, params);
            }
            catch (Error e)
            {
               throw e;
            }
            catch (Exception e)
            {
               throw e;
            }
            catch (Throwable t)
            {
               //only extremely wierd stuff!
               throw new Exception(t);
            }
         }
         
      } );
   }

}

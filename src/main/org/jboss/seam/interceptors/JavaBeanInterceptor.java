//$Id$
package org.jboss.seam.interceptors;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.InvocationContext;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * Adapts from CGLIB interception to Seam component interception
 * 
 * @author Gavin King
 */
public class JavaBeanInterceptor implements MethodInterceptor, Serializable
{
   
   private final SeamInterceptor seamInterceptor;
   private boolean recursive = false;
   
   public JavaBeanInterceptor(Component component)
   {
      seamInterceptor = new SeamInterceptor(InterceptorType.ANY, component);
   }

   public Object intercept(final Object target, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {
      if ( recursive || "finalize".equals( method.getName() ) ) 
      {
         return methodProxy.invokeSuper(target, params);
      }
      else
      {
         recursive = true;
         try
         {
            return interceptInvocation(target, method, params, methodProxy);
         }
         finally
         {
            recursive = false;
         }
      }
   }

   private Object interceptInvocation(final Object target, final Method method, final Object[] params, 
         final MethodProxy methodProxy) throws Exception
   {
      return seamInterceptor.aroundInvoke( new InvocationContext() {
         
         Object[] resultParams = params;
         final Map contextData = new HashMap();
         
         public Object getTarget()
         {
            return target;
         }
         
         public Map getContextData()
         {
            return contextData;
         }

         public Method getMethod()
         {
            return method;
         }

         public Object[] getParameters()
         {
            return params;
         }

         public Object proceed() throws Exception
         {
            try
            {
               return methodProxy.invokeSuper(target, resultParams);
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

         public void setParameters(Object[] newParams)
         {
            resultParams = newParams;
         }
         
      });
   }

}

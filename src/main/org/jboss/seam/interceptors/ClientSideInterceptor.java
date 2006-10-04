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
public class ClientSideInterceptor implements MethodInterceptor, Serializable
{
   
   private final SeamInterceptor seamInterceptor;
   private final Object bean;

   public ClientSideInterceptor(Object bean, Component component)
   {
      this.bean = bean;
      seamInterceptor = new SeamInterceptor(InterceptorType.CLIENT, component);
   }
   
   public Object intercept(final Object proxy, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {
      return interceptInvocation(bean, method, params, methodProxy);
   }

   private Object interceptInvocation(final Object bean, final Method method, final Object[] params, 
         final MethodProxy methodProxy) throws Exception
   {
      
      return seamInterceptor.aroundInvoke( new InvocationContext() {
         
         Object[] resultParams = params;
         final Map contextData = new HashMap();
         
         public Object getTarget()
         {
            return bean;
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
            SeamInterceptor.COMPONENT.set( seamInterceptor.getComponent() );
            try
            {
               return methodProxy.invoke(bean, resultParams);
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
            finally
            {
               SeamInterceptor.COMPONENT.set(null);
            }
         }

         public void setParameters(Object[] newParams)
         {
            resultParams = newParams;
         }
         
      });
   }

}

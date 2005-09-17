//$Id$
package org.jboss.seam.interceptors;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJBContext;
import javax.ejb.InvocationContext;

import org.jboss.seam.ejb.SeamInterceptor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Adapts from CGLIB interception to Seam component interception
 * 
 * @author Gavin King
 */
public class JavaBeanInterceptor implements MethodInterceptor, Serializable
{
   
   private final SeamInterceptor seamInterceptor = new SeamInterceptor();
   private boolean recursive = false;

   public Object intercept(final Object target, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {
      if (recursive) 
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
         
         public Object getBean()
         {
            return target;
         }
         
         public Map getContextData()
         {
            return contextData;
         }

         public EJBContext getEJBContext()
         {
            throw new UnsupportedOperationException("Not an EJB");
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

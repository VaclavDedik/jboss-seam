package org.jboss.seam.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.InvocationContext;

import net.sf.cglib.proxy.MethodProxy;

/**
 * InvocationContext for use with CGLIB-based interceptors.
 * 
 * @author Gavin King
 *
 */
public class RootInvocationContext implements InvocationContext
{
   private final Object bean;
   private final Method method;
   private Object[] params;
   private final Map contextData = new HashMap();
   private final MethodProxy methodProxy;

   public RootInvocationContext(Object bean, Method method, Object[] params, MethodProxy methodProxy)
   {
      this.bean = bean;
      this.method = method;
      this.params = params;
      this.methodProxy = methodProxy;
   }
   
   public RootInvocationContext(Object bean, Method method, Object[] params)
   {
      this(bean, method, params, null);
   }
   
   public Object proceed() throws Exception
   {
      if (methodProxy==null)
      {
         throw new UnsupportedOperationException();
      }
      
      try
      {
         return methodProxy.invoke(bean, params);
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

   public void setParameters(Object[] newParams)
   {
      params = newParams;
   }
}
package org.jboss.seam.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.InvocationContext;

/**
 * InvocationContext for use with CGLIB-based interceptors.
 * 
 * @author Gavin King
 *
 */
public abstract class RootInvocationContext implements InvocationContext
{
   private final Object bean;
   private final Method method;
   private Object[] params;
   private final Map contextData = new HashMap();

   public RootInvocationContext(Object bean, Method method, Object[] params)
   {
      this.bean = bean;
      this.method = method;
      this.params = params;
   }
   
   public abstract Object proceed() throws Exception;

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
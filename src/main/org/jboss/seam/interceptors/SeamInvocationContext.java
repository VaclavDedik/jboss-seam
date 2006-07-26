//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;

/**
 * Adapts from EJB interception to Seam component interceptors
 * 
 * @author Gavin King
 */
public class SeamInvocationContext implements InvocationContext
{
   
   private final InvocationContext ejbInvocationContext;
   private final List<Interceptor> interceptors;
   int location = 0;

   public SeamInvocationContext(InvocationContext ejbInvocationContext, List<Interceptor> interceptors)
   {
      this.ejbInvocationContext = ejbInvocationContext;
      this.interceptors = interceptors;
   }
   
   public Object getTarget()
   {
      return ejbInvocationContext.getTarget();
   }

   public Map getContextData()
   {
      return ejbInvocationContext.getContextData();
   }

   public Method getMethod()
   {
      return ejbInvocationContext.getMethod();
   }

   public Object[] getParameters()
   {
      return ejbInvocationContext.getParameters();
   }

   public Object proceed() throws Exception
   {
      if ( location==interceptors.size() )
      {
         return ejbInvocationContext.proceed();
      }
      else
      {
         return interceptors.get(location++).aroundInvoke(this);
      }
   }

   public void setParameters(Object[] params)
   {
      ejbInvocationContext.setParameters(params);
   }

}

//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.ejb.InvocationContext;

import org.jboss.seam.Component;

/**
 * Adapts from EJB interception to Seam component interceptors
 * 
 * @author Gavin King
 */
public class SeamInvocationContext implements InvocationContext
{
   
   public SeamInvocationContext(InvocationContext ejbInvocationContext, Component component)
   {
      this.component = component;
      this.ejbInvocationContext = ejbInvocationContext;
   }
   
   private final InvocationContext ejbInvocationContext;
   private final Component component;
   int location = 0;

   public Object getBean()
   {
      return ejbInvocationContext.getBean();
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
      
      List<Interceptor> interceptors = component.getInterceptors();
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

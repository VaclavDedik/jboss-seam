//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.interceptor.InvocationContext;

/**
 * Adapts from EJB interception to Seam component interceptors
 * 
 * @author Gavin King
 */
public class SeamInvocationContext implements InvocationContext
{
   
   private final EventType eventType;
   private final InvocationContext ejbInvocationContext;
   private final List<Interceptor> interceptors;
   private final List<Object> userInterceptors;
   int location = 0;

   public SeamInvocationContext(InvocationContext ejbInvocationContext, EventType type, List<Object> userInterceptors, List<Interceptor> interceptors)
   {
      this.ejbInvocationContext = ejbInvocationContext;
      this.interceptors = interceptors;
      this.userInterceptors = userInterceptors;
      this.eventType = type;
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
         Object userInterceptor = userInterceptors.get(location);
         Interceptor interceptor = interceptors.get(location);
         location++;
         switch(eventType)
         {
            case AROUND_INVOKE: return interceptor.aroundInvoke(this, userInterceptor);
            case POST_CONSTRUCT: return interceptor.postConstruct(this, userInterceptor);
            case PRE_DESTORY: return interceptor.preDestroy(this, userInterceptor);
            case PRE_PASSIVATE: return interceptor.prePassivate(this, userInterceptor);
            case POST_ACTIVATE: return interceptor.postActivate(this, userInterceptor);
            default: throw new IllegalArgumentException("no InvocationType");
         }
      }
   }

   public void setParameters(Object[] params)
   {
      ejbInvocationContext.setParameters(params);
   }

}

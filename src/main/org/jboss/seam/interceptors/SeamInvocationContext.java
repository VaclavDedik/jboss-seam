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
   int location = 0;

   public SeamInvocationContext(InvocationContext ejbInvocationContext, EventType type, List<Interceptor> interceptors)
   {
      this.ejbInvocationContext = ejbInvocationContext;
      this.interceptors = interceptors;
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
         Interceptor interceptor = interceptors.get(location++);
         switch(eventType)
         {
            case AROUND_INVOKE: return interceptor.aroundInvoke(this);
            case POST_CONSTRUCT: return interceptor.postConstruct(this);
            case PRE_DESTORY: return interceptor.preDestroy(this);
            default: throw new IllegalArgumentException("no InvocationType");
         }
      }
   }

   public void setParameters(Object[] params)
   {
      ejbInvocationContext.setParameters(params);
   }

}

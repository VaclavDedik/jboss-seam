package org.jboss.seam.intercept;

import java.util.List;

public class EE5SeamInvocationContext extends SeamInvocationContext implements javax.interceptor.InvocationContext
{

   public EE5SeamInvocationContext(InvocationContext context, EventType type, List<Object> userInterceptors, List<Interceptor> interceptors)
   {
      super(context, type, userInterceptors, interceptors);
   }

}

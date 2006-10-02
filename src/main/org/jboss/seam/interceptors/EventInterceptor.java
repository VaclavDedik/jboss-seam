package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.core.Events;

/**
 * Raises Seam events connected with a bean lifecycle.
 * 
 * @author Gavin King
 *
 */
@Interceptor(around={BijectionInterceptor.class, ConversationInterceptor.class, 
                     TransactionInterceptor.class, BusinessProcessInterceptor.class, 
                     RollbackInterceptor.class})
public class EventInterceptor extends AbstractInterceptor
{
   /*@PostConstruct
   public void postConstruct(InvocationContext ctx)
   {
      Events.instance().raiseEvent("org.jboss.seam.postConstruct." + component.getName());
   }

   @PreDestroy
   public void preDestroy(InvocationContext ctx)
   {
      Events.instance().raiseEvent("org.jboss.seam.preDestroy." + component.getName());
   }

   @PrePassivate
   public void prePassivate(InvocationContext ctx)
   {
      Events.instance().raiseEvent("org.jboss.seam.prePassivate." + component.getName());
   }
   
   @PostActivate
   public void postActivate(InvocationContext ctx)
   {
      Events.instance().raiseEvent("org.jboss.seam.postActivate." + component.getName());
   }*/
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext ctx) throws Exception
   {
      Object result = ctx.proceed();
      Method method = ctx.getMethod();
      if ( result!=null || method.getReturnType().equals(void.class) )
      {
         if ( method.isAnnotationPresent(RaiseEvent.class) )
         {
            String type = method.getAnnotation(RaiseEvent.class).value();
            if ( "".equals(type) ) type = method.getName();
            Events.instance().raiseEvent(type);
         }
      }
      return result;
   }
}

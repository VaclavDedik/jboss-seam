/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.intercept;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;

/**
 * Controller interceptor for server-side interceptors of
 * EJB3 session bean components.
 * 
 * @author Gavin King
 */
public class SessionBeanInterceptor extends RootInterceptor
{
   
   public static ThreadLocal<Component> COMPONENT = new ThreadLocal<Component>();

   /**
    * Called when instatiated by EJB container.
    * (In this case it might be a Seam component,
    * but we won't know until postConstruct() is
    * called.)
    */
   public SessionBeanInterceptor()
   {
      super(InterceptorType.SERVER);
   }
   
   @PrePassivate
   public void prePassivate(InvocationContext invocation)
   {
      invokeAndHandle(invocation, EventType.PRE_PASSIVATE);
   }
   
   @PostActivate
   public void postActivate(InvocationContext invocation)
   {
      invokeAndHandle(invocation, EventType.POST_ACTIVATE);
   }
   
   @PreDestroy
   public void preDestroy(InvocationContext invocation)
   {
      invokeAndHandle(invocation, EventType.PRE_DESTORY);
   }
   
   @PostConstruct
   public void postConstruct(InvocationContext invocation)
   {
      Component invokingComponent = SessionBeanInterceptor.COMPONENT.get();
      Object bean = invocation.getTarget();
      if ( invokingComponent!=null )
      {
         //the session bean was obtained by the application by
         //calling Component.getInstance(), could be a role
         //other than the default role
         init(invokingComponent);
      }
      else if ( bean.getClass().isAnnotationPresent(Name.class) )
      {
         //the session bean was obtained by the application from
         //JNDI, so assume the default role
         String defaultComponentName = bean.getClass().getAnnotation(Name.class).value();
         init( Seam.componentForName( defaultComponentName ) );
      }
      else
      {
         initNonSeamComponent();
      }
      
      postConstruct(bean);
      invokeAndHandle(invocation, EventType.POST_CONSTRUCT);
   }

}

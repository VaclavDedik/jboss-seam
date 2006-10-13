//$Id$
package org.jboss.seam.intercept;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * Controller interceptor for client-side interceptors of
 * EJB3 session bean components
 * 
 * @author Gavin King
 */
public class ClientSideInterceptor extends RootInterceptor 
      implements MethodInterceptor
{
   
   private final Object bean;

   public ClientSideInterceptor(Object bean, Component component)
   {
      super(InterceptorType.CLIENT);
      this.bean = bean;
      init(component);
   }
   
   public Object intercept(final Object proxy, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {
      String methodName = method.getName();
      if ( params!=null && params.length==0 )
      {
         if ( "finalize".equals(methodName) )
         {
            return methodProxy.invokeSuper(proxy, params);
         }
         else if ( "writeReplace".equals(methodName) )
         {
            return this;
         }
      }
      Object result = interceptInvocation(method, params, methodProxy);
      return sessionBeanReturnedThis(result) ? proxy : result;
   }

   private boolean sessionBeanReturnedThis(Object result)
   {
      return result==bean || (
            result!=null && getComponent().getBeanClass().isAssignableFrom( result.getClass() )
         );
   }

   private Object interceptInvocation(final Method method, final Object[] params, final MethodProxy methodProxy) throws Exception
   {
      return aroundInvoke( new RootInvocationContext(bean, method, params, methodProxy)
      {
         public Object proceed() throws Exception
         {
            SeamInterceptor.COMPONENT.set( getComponent() );
            try
            {
               return super.proceed();
            }
            finally
            {
               SeamInterceptor.COMPONENT.set(null);
            }
         }
      
      });
   }
   
   //TODO: copy/paste from JavaBean interceptor
   Object readResolve()
   {
      Component comp = getComponent();
      if (comp==null)
      {
         throw new IllegalStateException("No component found: " + getComponentName());
      }
      
      try
      {
         return comp.wrap(bean, this);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}

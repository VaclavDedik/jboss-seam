//$Id$
package org.jboss.seam.intercept;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;

/**
 * Controller interceptor for JavaBean components
 * 
 * @author Gavin King
 */
public class JavaBeanInterceptor extends RootInterceptor
      implements MethodInterceptor
{
   
   private final Object bean;
   
   public JavaBeanInterceptor(Object bean, Component component)
   {
      super(InterceptorType.ANY);
      this.bean = bean;
      init(component);
   }

   public Object intercept(final Object proxy, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {

      String methodName = method.getName();
      if ( "finalize".equals(methodName) ) 
      {
         return methodProxy.invokeSuper(proxy, params);
      }
      else if ( "writeReplace".equals(methodName) )
      {
         return this;
      }
      else if ( "sessionDidActivate".equals(methodName) )
      {
         callPostActivate();
         return null;
      }
      else if ( "sessionWillPassivate".equals(methodName) )
      {
         callPrePassivate();
         return null;
      }
      else
      {
         return interceptInvocation(method, params, methodProxy);
      }

   }

   private void callPrePassivate()
   {
      prePassivate( new RootInvocationContext(bean, getComponent().getPrePassivateMethod(), new Object[0])
      {
         public Object proceed() throws Exception
         {
            getComponent().callPrePassivateMethod(bean);
            return null;
         }
         
      } );
   }

   private void callPostActivate()
   {
      postActivate( new RootInvocationContext(bean, getComponent().getPostActivateMethod(), new Object[0])
      {
         public Object proceed() throws Exception
         {
            getComponent().callPostActivateMethod(bean);
            return null;
         }
         
      } );
   }

   private Object interceptInvocation(final Method method, final Object[] params, 
         final MethodProxy methodProxy) throws Exception
   {
      return aroundInvoke( new RootInvocationContext(bean, method, params, methodProxy) );
   }
   
   // TODO: copy/paste from ClientSide interceptor
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

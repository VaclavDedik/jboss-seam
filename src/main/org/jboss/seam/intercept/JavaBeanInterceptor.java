//$Id$
package org.jboss.seam.intercept;

import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.core.Mutable;

/**
 * Controller interceptor for JavaBean components
 * 
 * @author Gavin King
 */
public class JavaBeanInterceptor extends RootInterceptor
      implements MethodInterceptor
{
   
   private final Object bean;
   private transient boolean dirty;
   
   public JavaBeanInterceptor(Object bean, Component component)
   {
      super(InterceptorType.ANY);
      this.bean = bean;
      init(component);
   }

   public Object intercept(final Object proxy, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {

      if ( params!=null && params.length==0 )
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
         else if ( "clearDirty".equals(methodName) && !(bean instanceof Mutable) )
         {
            boolean result = dirty;
            dirty = false;
            return result;
         }
      }

      //mark it dirty each time it gets called 
      //TODO: we could support an @ReadOnly annotation
      dirty = true; 
         
      Object result = interceptInvocation(method, params, methodProxy);
      return result==bean ? proxy : result;

   }
   
   public void postConstruct()
   {
      super.postConstruct(bean);
      callPostConstruct();
   }

   private void callPostConstruct()
   {
      InvocationContext context = new RootInvocationContext( bean, getComponent().getPostConstructMethod(), new Object[0] )
      {
         @Override
         public Object proceed() throws Exception
         {
            getComponent().callPostConstructMethod(bean);
            return null;
         }
         
      };
      invokeAndHandle(context, EventType.POST_CONSTRUCT);
   }

   private void callPrePassivate()
   {
      InvocationContext context = new RootInvocationContext( bean, getComponent().getPrePassivateMethod(), new Object[0] )
      {
         @Override
         public Object proceed() throws Exception
         {
            getComponent().callPrePassivateMethod(bean);
            return null;
         }
         
      };
      invokeAndHandle(context, EventType.PRE_PASSIVATE);
   }

   private void callPostActivate()
   {
      RootInvocationContext context = new RootInvocationContext(bean, getComponent().getPostActivateMethod(), new Object[0])
      {
         @Override
         public Object proceed() throws Exception
         {
            getComponent().callPostActivateMethod(bean);
            return null;
         }
         
      };
      invokeAndHandle(context, EventType.POST_ACTIVATE);
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

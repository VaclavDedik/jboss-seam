//$Id$
package org.jboss.seam.interceptors;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.InvocationContext;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * Adapts from CGLIB interception to Seam component interception
 * 
 * @author Gavin King
 */
public class JavaBeanInterceptor implements MethodInterceptor, Serializable
{
   
   private final SeamInterceptor seamInterceptor;
   private boolean recursive = false;
   private final Component component;
   
   public JavaBeanInterceptor(Component component)
   {
      seamInterceptor = new SeamInterceptor(InterceptorType.ANY, component);
      this.component = component;
   }

   public Object intercept(final Object target, final Method method, final Object[] params,
         final MethodProxy methodProxy) throws Throwable
   {
      if (recursive) 
      {
         return methodProxy.invokeSuper(target, params);
      }
      
      recursive = true;
      try
      {
         String methodName = method.getName();
         if ( "finalize".equals(methodName) ) 
         {
            return methodProxy.invokeSuper(target, params);
         }
         else if ( "sessionDidActivate".equals(methodName) )
         {
            callPostActivate(target);
            return null;
         }
         else if ( "sessionWillPassivate".equals(methodName) )
         {
            callPrePassivate(target);
            return null;
         }
         else
         {
            return interceptInvocation(target, method, params, methodProxy);
         }
      }
      finally
      {
         recursive = false;
      }
   }

   private void callPrePassivate(final Object target)
   {
      seamInterceptor.prePassivate( new InvocationContext() {
         
         final Object[] params = {};
         final Method passivateMethod = component.getPrePassivateMethod();
         final Map contextData = new HashMap();
         
         public Object getTarget()
         {
            return target;
         }
         
         public Map getContextData()
         {
            return contextData;
         }

         public Method getMethod()
         {
            return passivateMethod;
         }

         public Object[] getParameters()
         {
            return params;
         }

         public Object proceed() throws Exception
         {
            component.callPrePassivateMethod(target);
            return null;
         }

         public void setParameters(Object[] newParams)
         {
            throw new IllegalArgumentException();
         }
         
      } );
   }

   private void callPostActivate( final Object target)
   {
      seamInterceptor.postActivate( new InvocationContext() {
         
         final Object[] params = {};
         final Method activateMethod = component.getPostActivateMethod();
         final Map contextData = new HashMap();
         
         public Object getTarget()
         {
            return target;
         }
         
         public Map getContextData()
         {
            return contextData;
         }

         public Method getMethod()
         {
            return activateMethod;
         }

         public Object[] getParameters()
         {
            return params;
         }

         public Object proceed() throws Exception
         {
            component.callPostActivateMethod(target);
            return null;
         }

         public void setParameters(Object[] newParams)
         {
            throw new IllegalArgumentException();
         }
         
      } );
   }

   private Object interceptInvocation(final Object target, final Method method, final Object[] params, 
         final MethodProxy methodProxy) throws Exception
   {
      return seamInterceptor.aroundInvoke( new InvocationContext() {
         
         Object[] resultParams = params;
         final Map contextData = new HashMap();
         
         public Object getTarget()
         {
            return target;
         }
         
         public Map getContextData()
         {
            return contextData;
         }

         public Method getMethod()
         {
            return method;
         }

         public Object[] getParameters()
         {
            return resultParams;
         }

         public Object proceed() throws Exception
         {
            try
            {
               return methodProxy.invokeSuper(target, resultParams);
            }
            catch (Error e)
            {
               throw e;
            }
            catch (Exception e)
            {
               throw e;
            }
            catch (Throwable t)
            {
               //only extremely wierd stuff!
               throw new Exception(t);
            }
         }

         public void setParameters(Object[] newParams)
         {
            resultParams = newParams;
         }
         
      } );
   }

}

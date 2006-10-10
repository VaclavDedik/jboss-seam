//$Id$
package org.jboss.seam.intercept;

import java.io.Serializable;
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
      implements MethodInterceptor, Serializable
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
      //TODO: handle the finalize method
      return aroundInvoke( new RootInvocationContext(bean, method, params)
      {
         public Object proceed() throws Exception
         {
            SeamInterceptor.COMPONENT.set( getComponent() );
            try
            {
               return methodProxy.invoke(bean, params);
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
            finally
            {
               SeamInterceptor.COMPONENT.set(null);
            }
         }
      
      });
   }

}

package org.jboss.seam.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;

public class ResponseInvocationHandler implements InvocationHandler {
   
   private static final Method SEND_REDIRECT;
   static
   {
      try
      {
         SEND_REDIRECT = HttpServletResponse.class.getMethod("sendRedirect", String.class);
      }
      catch (Exception e)
      {
         throw new RuntimeException("sendRedirect() method not found", e);
      }
   }
   
   private Object response;
   
   private ResponseInvocationHandler(Object object) {
      this.response = object;
   }
   
   public Object invoke(Object proxy, Method method, Object[] args)
         throws Throwable {
      if ( method.equals(SEND_REDIRECT) )
      {
         if ( Contexts.isEventContextActive() )
         {
            String url = (String) args[0];
            if ( !url.contains("?conversationId=") )
            {
               args[0] = Manager.instance().encodeConversationId(url);
            }
         }
      }
      return method.invoke(response, args);
   }
   
   public static ServletResponse proxyResponse(ServletResponse response)
   {
      return (ServletResponse) Proxy.newProxyInstance(
            ServletResponse.class.getClassLoader(), 
            new Class[] {HttpServletResponse.class, ServletResponse.class}, 
            new ResponseInvocationHandler(response)
         );
   }

}

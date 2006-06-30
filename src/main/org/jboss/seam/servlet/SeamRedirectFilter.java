package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;

/**
 * Propagates the conversation context across a browser redirect
 * 
 * @author Gavin King
 */
public class SeamRedirectFilter implements Filter 
{

   public void init(FilterConfig config) throws ServletException {}

   public void doFilter(ServletRequest request, ServletResponse response,
         FilterChain chain) throws IOException, ServletException 
   {
      chain.doFilter( request, wrapResponse( (HttpServletResponse) response ) );
   }
   
   private static ServletResponse wrapResponse(HttpServletResponse response) 
   {
      return new HttpServletResponseWrapper(response)
      {
         @Override
         public void sendRedirect(String url) throws IOException
         {
            if ( Contexts.isEventContextActive() )
            {
               Manager manager = Manager.instance();
               if ( !url.contains("?" + manager.getConversationIdParameter() +"=") )
               {
                  url = manager.encodeConversationId(url);
                  manager.beforeRedirect();
               }
            }
            super.sendRedirect(url);
         }  
      };
   }

   public void destroy() {}

}

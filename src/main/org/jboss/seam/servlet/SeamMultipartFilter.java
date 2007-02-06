package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebApplicationContext;

public class SeamMultipartFilter extends SeamFilter
{
   public static final String MULTIPART = "multipart/";
   
   private MultipartConfig config;
  
   @Override
   public void init(FilterConfig filterConfig) 
       throws ServletException
   {
      super.init(filterConfig);      
      
      Context appContext = new WebApplicationContext(getServletContext());
      config = (MultipartConfig) appContext.get(MultipartConfig.class);
   }
   
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
   {
      if (!(response instanceof HttpServletResponse))
      {
         chain.doFilter(request, response);
         return;
      }

      HttpServletRequest httpRequest = (HttpServletRequest) request;

      if (isMultipartRequest(httpRequest))
      {
         chain.doFilter(new MultipartRequest(httpRequest, config), response);
      }
      else
      {
         chain.doFilter(request, response);
      }
   }
   
   private boolean isMultipartRequest(HttpServletRequest request)
   {
      if (!"post".equals(request.getMethod().toLowerCase()))
      {
         return false;
      }
      
      String contentType = request.getContentType();
      if (contentType == null)
      {
         return false;
      }
      
      if (contentType.toLowerCase().startsWith(MULTIPART))
      {
         return true;
      }
      
      return false;     
   }

}

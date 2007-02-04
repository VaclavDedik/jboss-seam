package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SeamMultipartFilter extends SeamFilter
{
   public static final String MULTIPART = "multipart/";
   
   private boolean createTempFiles = false;
  
   @Override
   public void init(FilterConfig filterConfig) 
       throws ServletException
   {
      super.init(filterConfig);
      String param = filterConfig.getInitParameter("createTempFiles");
      if (param != null) 
      {
         createTempFiles = "true".equals(param);
      }
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
         chain.doFilter(new MultipartRequest(httpRequest, createTempFiles), response);
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

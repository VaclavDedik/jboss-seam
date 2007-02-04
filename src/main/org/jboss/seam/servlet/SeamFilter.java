package org.jboss.seam.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public abstract class SeamFilter implements Filter
{

   private ServletContext servletContext;

   public void init(FilterConfig filterConfig) throws ServletException 
   {
      servletContext = filterConfig.getServletContext();
   }

   public void destroy() {}

   protected ServletContext getServletContext()
   {
      return servletContext;
   }

}

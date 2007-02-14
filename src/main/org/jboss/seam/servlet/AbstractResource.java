package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractResource
{
   private ServletContext context;
   
   protected ServletContext getServletContext()
   {
      return context;
   }
   
   protected void setServletContext(ServletContext context)
   {
      this.context = context;
   }
         
   public abstract void getResource(HttpServletRequest request, HttpServletResponse response)
       throws IOException;
   
   protected abstract String getResourcePath();
}

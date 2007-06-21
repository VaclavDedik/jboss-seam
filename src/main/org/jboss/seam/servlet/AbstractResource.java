package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Superclass of Seam components that serve up "resources" to the client 
 * via the Seam resource servlet.
 * 
 * @author Shane Bryzak
 *
 */
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
       throws ServletException, IOException;
   
   protected abstract String getResourcePath();
}

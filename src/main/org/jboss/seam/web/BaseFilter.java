package org.jboss.seam.web;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public abstract class BaseFilter implements Filter
{
   private ServletContext servletContext;
   
   /**
    * By default the filter is enabled
    */
   private boolean disabled = false;
   
   /**
    * By default match all requests
    */
   private String urlPattern = "/*";    

   public void init(FilterConfig filterConfig) throws ServletException
   {
      servletContext = filterConfig.getServletContext();
   }
   
   protected ServletContext getServletContext()
   {
      return servletContext;
   }
   
   public String getUrlPattern()
   {
      return urlPattern;
   }
   
   public void setUrlPattern(String urlPattern)
   {
      this.urlPattern = urlPattern;
   }
   
   public boolean isDisabled()
   {
      return disabled;
   }
   
   public void setDisabled(boolean disabled)
   {
      this.disabled = disabled;
   }   
   
   
   /**
    * Pattern matching code, adapted from Tomcat. This method checks to see if
    * the specified path matches the specified pattern.
    * 
    * @param request ServletRequest The request containing the path
    * @return boolean True if the path matches the pattern, false otherwise
    */
   boolean matchesRequestPath(ServletRequest request)
   {
      if (!(request instanceof HttpServletRequest))
         return true;
      
      String path = ((HttpServletRequest) request).getServletPath();      
      String pattern = getUrlPattern();

      if (path == null || "".equals(path)) path = "/";
      if (pattern == null || "".equals(pattern)) pattern = "/";

      // Check for an exact match
      if (path.equals(pattern)) return true;

      // Check for path prefix matching
      if (pattern.startsWith("/") && pattern.endsWith("/*"))
      {
         pattern = pattern.substring(0, pattern.length() - 2);
         if (pattern.length() == 0) return true;

         if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

         while (true)
         {
            if (pattern.equals(path)) return true;
            int slash = path.lastIndexOf('/');
            if (slash <= 0) break;
            path = path.substring(0, slash);
         }
         return false;
      }

      // Check for suffix matching
      if (pattern.startsWith("*."))
      {
         int slash = path.lastIndexOf('/');
         int period = path.lastIndexOf('.');
         if ((slash >= 0) && (period > slash) && path.endsWith(pattern.substring(1)))
         {
            return true;
         }
         return false;
      }

      // Check for universal mapping
      if (pattern.equals("/")) return true;

      return false;
   }
   
   public void destroy() {}
   
}

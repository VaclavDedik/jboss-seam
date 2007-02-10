package org.jboss.seam.web;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.servlet.multipartFilter")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class MultipartFilter extends AbstractFilter
{
   public static final String MULTIPART = "multipart/";
   
   /**
    * Flag indicating whether a temporary file should be used to cache the uploaded file
    */
   private boolean createTempFiles = false;
   
   /**
    * The maximum size of a file upload request.  0 means no limit.
    */
   private int maxRequestSize = 0; 
     
   public boolean getCreateTempFiles()
   {
      return createTempFiles;
   }
   
   public void setCreateTempFiles(boolean createTempFiles)
   {
      this.createTempFiles = createTempFiles;
   }
   
   public int getMaxRequestSize()
   {
      return maxRequestSize;
   }
   
   public void setMaxRequestSize(int maxFileSize)
   {
      this.maxRequestSize = maxFileSize;
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
         chain.doFilter(new MultipartRequest(httpRequest, createTempFiles, 
                  maxRequestSize), response);
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

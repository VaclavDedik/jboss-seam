package org.jboss.seam.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebApplicationContext;

public class SeamFilter implements Filter
{
   private ServletContext servletContext;
   
   private Set<Filter> filters = new HashSet<Filter>();   
   
   private boolean disabled = false;
   private String urlPattern = null; 
   
   private class FilterChainImpl implements FilterChain
   {      
      private Iterator<Filter> iter;
      
      private ServletRequest request;
      private ServletResponse response;
      
      public FilterChainImpl(Iterator<Filter> iter)
      {
        this.iter = iter;  
      }
      
      public ServletRequest getRequest()
      {
         return request;
      }
      
      public ServletResponse getResponse()
      {
         return response;
      }
      
      public void doFilter(ServletRequest request, ServletResponse response)
          throws IOException, ServletException
      {
         this.request = request;
         this.response = response;
         
         if (iter.hasNext())
         {
            Filter filter = iter.next();
            
            if (filter instanceof SeamFilter)
            {
               SeamFilter sf = (SeamFilter) filter;
               if (sf.getUrlPattern() == null || sf.matchesRequestPath(request))
               {
                  filter.doFilter(request, response, this);
               }
               else
               {
                  this.doFilter(request, response);
               }
            }            
            else
            {
               filter.doFilter(request, response, this);
            }
         }
      }
   }

   protected ServletContext getServletContext()
   {
      return servletContext;
   }   
   
   public void init(FilterConfig filterConfig) 
      throws ServletException 
   {
      servletContext = filterConfig.getServletContext();      
      initFilters();
   }
   
   protected void initFilters()
   {
      if (getClass().equals(SeamFilter.class)) 
      {
         Context ctx = new WebApplicationContext(servletContext); 
         
         addFilter((Filter) ctx.get(MultipartFilter.class));
      }
   }
   
   public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain)
       throws IOException, ServletException
   {
      FilterChainImpl fc = new FilterChainImpl(filters.iterator());
      fc.doFilter(request, response);
      chain.doFilter(fc.getRequest(), fc.getResponse());
   }
   
   protected boolean addFilter(Filter filter)
   {
      if (filter instanceof SeamFilter && ((SeamFilter) filter).isDisabled())
         return false;
         
      return filters.add(filter);
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
   private boolean matchesRequestPath(ServletRequest request)
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

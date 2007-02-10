package org.jboss.seam.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public class SeamFilter implements Filter
{
   private static final LogProvider log = Logging.getLogProvider(SeamFilter.class);   
   
   private ServletContext servletContext;
   
   private List<Filter> filters = new ArrayList<Filter>();   
   
   private class FilterChainImpl implements FilterChain
   {  
      private FilterChain chain;      
      private Iterator<Filter> iter;
           
      public FilterChainImpl(FilterChain chain, Iterator<Filter> iter)
      {
         this.chain = chain;
         this.iter = iter;  
      }
      
      public void doFilter(ServletRequest request, ServletResponse response)
          throws IOException, ServletException
      {         
         if (iter.hasNext())
         {
            Filter filter = iter.next();
            
            if (filter instanceof BaseFilter)
            {
               BaseFilter bf = (BaseFilter) filter;
               if (bf.getUrlPattern() == null || bf.matchesRequestPath(request))
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
         else
         {
            chain.doFilter(request, response);
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
      initFilters(filterConfig);
   }
   
   protected void initFilters(FilterConfig filterConfig)
      throws ServletException
   {
      Context ctx = new WebApplicationContext(servletContext); 
      
      Init init = (Init) ctx.get(Init.class);
      for (Class filterClass : init.getInstalledFilters())
      {
         log.info("Installed filter " + filterClass.getName());
         addFilter((Filter) ctx.get(filterClass), filterConfig);
      }
   }
   
   public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain)
       throws IOException, ServletException
   {
      new FilterChainImpl(chain, filters.iterator()).doFilter(request, response);
   }
   
   protected boolean addFilter(Filter filter, FilterConfig filterConfig)
      throws ServletException
   {
      if (filter instanceof BaseFilter && ((BaseFilter) filter).isDisabled())
         return false;
         
      filter.init(filterConfig);
      return filters.add(filter);
   }     

   public void destroy() {}
}

package org.jboss.seam.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.SortItem;
import org.jboss.seam.util.Sorter;
import org.jboss.seam.web.AbstractFilter;

/**
 * A servlet filter that orchestrates the stack of Seam
 * component filters, and controls ordering. Filter
 * ordering is specified via the @Filter annotation.
 * Filters may optionally extend AbstractFilter.
 * 
 * @see org.jboss.seam.annotations.web.Filter
 * @see AbstractFilter
 * 
 * @author Shane Bryzak
 * @author Pete Muir
 *
 */
public class SeamFilter implements Filter
{
   private static final LogProvider log = Logging.getLogProvider(SeamFilter.class);   
   
   private List<Filter> filters;
   
   private class FilterChainImpl implements FilterChain
   {  
      private FilterChain chain;
      private int index;
           
      private FilterChainImpl(FilterChain chain)
      {
         this.chain = chain;
         index = -1;
      }
      
      public void doFilter(ServletRequest request, ServletResponse response)
          throws IOException, ServletException
      {
         if ( ++index < filters.size() )
         {
            Filter filter = filters.get(index);
            
            if (filter instanceof AbstractFilter)
            {
               AbstractFilter bf = (AbstractFilter) filter;
               if ( bf.isMappedToCurrentRequestPath(request) )
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

   public void init(FilterConfig filterConfig) throws ServletException 
   {
      Lifecycle.mockApplication();
      try
      {
         filters = getSortedFilters();
         for ( Filter filter : filters )
         {
            log.info( "Initializing filter: " + Component.getComponentName(filter.getClass()));
            filter.init(filterConfig);
         }
      }
      finally
      {
         Lifecycle.unmockApplication();
      }
   }

   private List<Filter> getSortedFilters()
   {
      List<SortItem<Filter>> sortable = new ArrayList<SortItem<Filter>>(); 
      //retrieve the Filter instances from the application context
      
      
      for (final String filterName : Init.instance().getInstalledFilters())
      {
         
         sortable.add(new SortItem<Filter>()
         {

            private Filter filter = (Filter) Component.getInstance(filterName, ScopeType.APPLICATION);
            
            @Override
            public List<String> getAround()
            {
               return Arrays.asList( getFilterAnnotation( filter.getClass() ).around() );
            }

            @Override
            protected Filter getObject()
            {
               return filter;
            }

            @Override
            public List<String> getWithin()
            {
               return Arrays.asList( getFilterAnnotation( filter.getClass() ).within() );
            }
            
            @Override
            public boolean isAddable()
            {
               if (filter instanceof AbstractFilter)
               {
                  return !((AbstractFilter) filter).isDisabled();
               }
               else
               {
                  return true;
               }
            }
            
            @Override
            public String getKey()
            {
               return filter.getClass().getName();
            }
 
            
         });
      }
      return new Sorter<Filter>().sort(sortable);
      
   }
   
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
       throws IOException, ServletException
   {
      new FilterChainImpl(chain).doFilter(request, response);
   }
   
   public void destroy() 
   {
      for (Filter filter: filters)
      {
         filter.destroy();
      }
   }
   
   private org.jboss.seam.annotations.web.Filter getFilterAnnotation(Class<?> clazz)
   {
      while (!Object.class.equals(clazz))
      {
         if (clazz.isAnnotationPresent(org.jboss.seam.annotations.web.Filter.class))
         {
            return clazz.getAnnotation(org.jboss.seam.annotations.web.Filter.class);
         }
         else
         {
            clazz = clazz.getSuperclass();
         }
      }
      return null;
   }
   
}

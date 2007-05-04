package org.jboss.seam.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.SortItem;
import org.jboss.seam.util.SorterNew;

public class SeamFilter implements Filter
{
   private static final LogProvider log = Logging.getLogProvider(SeamFilter.class);   
   
   private List<Filter> filters = new ArrayList<Filter>();   
   
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
               if ( bf.matchesRequestPath(request) )
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
      Context tempApplicationContext = new WebApplicationContext( filterConfig.getServletContext() ); 
      Init init = (Init) tempApplicationContext.get(Init.class);
      
      // Setup ready for sorting
      Map<String, SortItem<Filter>> sortItemsMap = new HashMap<String, SortItem<Filter>>();
      List<SortItem<Filter>> sortItems = new ArrayList<SortItem<Filter>>();
      
      for (String filterName : init.getInstalledFilters())
      {
         Filter filter = (Filter) tempApplicationContext.get(filterName);
         SortItem<Filter> si = new SortItem<Filter>(filter);         
         sortItemsMap.put(filterName, si);
         sortItems.add(si);
      }

      for (SortItem<Filter> sortItem : sortItems)
      {
         org.jboss.seam.annotations.Filter filterAnn = getFilterAnnotation(sortItem.getObj().getClass());
         if ( filterAnn != null )
         {
            for (String s : Arrays.asList( filterAnn.around() ) )
            {
               SortItem<Filter> aroundSortItem = sortItemsMap.get(s);
               if (sortItem!=null && aroundSortItem != null) sortItem.getAround().add( aroundSortItem );
            }
            for (String s : Arrays.asList( filterAnn.within() ) )
            {
               SortItem<Filter> withinSortItem = sortItemsMap.get(s);
               if (sortItem!=null && withinSortItem != null) sortItem.getWithin().add( withinSortItem );
            }
         }
      }

      // Do the sort
      SorterNew<Filter> sList = new SorterNew<Filter>();
      sortItems = sList.sort(sortItems);
      
      // Set the sorted filters, initialize them
      for (SortItem<Filter> sortItem : sortItems)
      {
         Filter filter = sortItem.getObj();
         filters.add(filter);
         log.info( "Initializing filter: " + Component.getComponentName(filter.getClass()));
         filter.init(filterConfig);
      }
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
   
   private org.jboss.seam.annotations.Filter getFilterAnnotation(Class<?> clazz)
   {
      while (!Object.class.equals(clazz))
      {
         if (clazz.isAnnotationPresent(org.jboss.seam.annotations.Filter.class))
         {
            return clazz.getAnnotation(org.jboss.seam.annotations.Filter.class);
         }
         else
         {
            clazz = clazz.getSuperclass();
         }
      }
      return null;
   }
   
}

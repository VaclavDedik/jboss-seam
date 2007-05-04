package org.jboss.seam.ui.filter;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.util.EnumerationEnumeration;
import org.jboss.seam.web.AbstractAjax4jsfFilter;

@Name("org.jboss.seam.web.ajax4jsfFilter")
@Install(precedence = FRAMEWORK)
@Startup
public class Ajax4jsfFilter extends AbstractAjax4jsfFilter
{
   
   private class FilterConfigWrapper implements FilterConfig
   {
      
      private FilterConfig delegate;
      
      private Map<String, String> parameters;
      
      public FilterConfigWrapper (FilterConfig filterConfig)
      {
         delegate = filterConfig;
         parameters = new HashMap<String, String>();
      }

      public String getFilterName()
      {
         return delegate.getFilterName();
      }

      public String getInitParameter(String name)
      {
         if (parameters.containsKey(name))
         {
            return parameters.get(name);
         }
         else
         {
            return delegate.getInitParameter(name);
         }
      }

      public Enumeration getInitParameterNames()
      {
         Enumeration[] enumerations = {delegate.getInitParameterNames(), Collections.enumeration(parameters.keySet())};
         return new EnumerationEnumeration(enumerations);
      }

      public ServletContext getServletContext()
      {
         return delegate.getServletContext();
      }
      
      public FilterConfig addParameter(String name, String value)
      {
         parameters.put(name, value);
         return this;
      }
   }
   
   @Create
   public void create()
   {
      delegate = new org.ajax4jsf.Filter();
   }
   
   @Override
   protected FilterConfig initFilterConfig(FilterConfig filterConfig)
   {
      FilterConfigWrapper filterConfigWrapper = new FilterConfigWrapper(filterConfig);
      if (getForceParser() != null)
      {
         filterConfigWrapper.addParameter("forceparser", getForceParser());
      }
      if (getEnableCache() != null)
      {
         filterConfigWrapper.addParameter("enable-cache", getEnableCache());
      }
      if (getLog4jInitFile() != null)
      {
         filterConfigWrapper.addParameter("log4j-init-file", getLog4jInitFile());
      }
      return filterConfigWrapper;
   }

}

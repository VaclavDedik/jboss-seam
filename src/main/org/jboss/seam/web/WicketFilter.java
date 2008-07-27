package org.jboss.seam.web;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

@Scope(APPLICATION)
@Name("org.jboss.seam.web.wicketFilter")
@Install(precedence = BUILT_IN, dependencies="org.jboss.seam.wicket.web.wicketFilterInstantiator")
@BypassInterceptors
@org.jboss.seam.annotations.web.Filter
public class WicketFilter extends AbstractFilter
{
   
   private Filter delegate = null;

   private String applicationClass;
   
   private String applicationFactoryClass;
   
   private boolean detectPortletContext;
   
   
   
   public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain chain) throws IOException, ServletException
   {
      if (delegate==null)
      {
         chain.doFilter(servletRequest, servletResponse);
      }
      else
      {
         new ContextualHttpServletRequest((HttpServletRequest) servletRequest)
         {
            @Override
            public void process() throws Exception 
            {
               delegate.doFilter(servletRequest, servletResponse, chain);
            }
            
         }.run();
      }
   }
   
   @Override
   public void init(FilterConfig filterConfig) throws ServletException
   {  
      super.init(filterConfig);
      
      delegate = (javax.servlet.Filter) Component.getInstance("org.jboss.seam.wicket.web.wicketFilterInstantiator", ScopeType.STATELESS);
      
      if (delegate!=null)
      {
         Map<String, String> parameters = new HashMap<String, String>();
         if ( getApplicationClass() != null )
         {
            parameters.put( "applicationClassName", getApplicationClass() );
         }
         if ( getUrlPattern() != null )
         {
            parameters.put("filterMappingUrlPattern", getUrlPattern());
         }
         else
         {
            parameters.put("filterMappingUrlPattern", "/*");
         }
         if (getApplicationFactoryClass() != null)
         {
            parameters.put("applicationFactoryClassName", getApplicationFactoryClass());
         }
         if (isDetectPortletContext())
         {
            parameters.put("detectPortletContext", "true");
         }
         delegate.init( new FilterConfigWrapper(filterConfig, parameters) );
      }
   }
   
   public String getApplicationClass()
   {
      return applicationClass;
   }
   
   public void setApplicationClass(String applicationClassName)
   {
      this.applicationClass = applicationClassName;
   }
   
   public String getApplicationFactoryClass()
   {
      return applicationFactoryClass;
   }
   
   public void setApplicationFactoryClass(String applicationFactoryClass)
   {
      this.applicationFactoryClass = applicationFactoryClass;
   }
   
   public boolean isDetectPortletContext()
   {
      return detectPortletContext;
   }
   
   public void setDetectPortletContext(boolean detectPortletContext)
   {
      this.detectPortletContext = detectPortletContext;
   }
   
}

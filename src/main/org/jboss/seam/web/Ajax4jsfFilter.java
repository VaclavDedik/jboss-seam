package org.jboss.seam.web;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.annotations.Filter;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Scope(APPLICATION)
@Name("org.jboss.seam.web.ajax4jsfFilter")
@Install(precedence = BUILT_IN, classDependencies="org.ajax4jsf.Filter")
@BypassInterceptors
@Filter
public class Ajax4jsfFilter extends AbstractFilter
{
   
   private String forceParser;
   private String enableCache;
   private String log4jInitFile;
   
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException
   {
      delegate.doFilter(servletRequest, servletResponse, chain);
   }
   
   protected javax.servlet.Filter delegate;
   
   @Override
   public void init(FilterConfig filterConfig) throws ServletException
   {  
      filterConfig = initFilterConfig(filterConfig);
      super.init(filterConfig);
      delegate.init(filterConfig);
   }
   
   protected FilterConfig initFilterConfig(FilterConfig filterConfig)
   {
      return filterConfig;
   }

   public String getEnableCache()
   {
      return enableCache;
   }

   public void setEnableCache(String enableCache)
   {
      this.enableCache = enableCache;
   }

   public String getForceParser()
   {
      return forceParser;
   }

   public void setForceParser(String forceParser)
   {
      this.forceParser = forceParser;
   }
   
   public String getForceparser()
   {
      return forceParser;
   }
   
   public void setForceparser(String forceParser)
   {
      this.forceParser = forceParser;
   }

   public String getLog4jInitFile()
   {
      return log4jInitFile;
   }

   public void setLog4jInitFile(String log4jInitFile)
   {
      this.log4jInitFile = log4jInitFile;
   }

}

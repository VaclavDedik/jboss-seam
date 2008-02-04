package org.jboss.seam.wicket;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.web.AbstractFilter;

/**
 * 
 * @author Kill the redirect filter
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.web.redirectFilter")
@Install(precedence = 100, classDependencies="org.apache.wicket.Application")
@BypassInterceptors
@Filter(within="org.jboss.seam.web.ajax4jsfFilter")
public class WicketRedirectFilter extends AbstractFilter 
{
   
   
   @Override
   public boolean isDisabled()
   {
      return true;
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
   {
      
      
   }
}

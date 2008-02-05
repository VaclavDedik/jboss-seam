package org.jboss.seam.wicket.web;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.web.AbstractFilter;

/**
 * Disable the redirect filter when using Wicket (as JSF is an EE library, we
 * can't rely on classDependencies to disable it)
 * 
 * @author 
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.web.redirectFilter")
@Install(precedence = FRAMEWORK, classDependencies="org.apache.wicket.Application")
@BypassInterceptors
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

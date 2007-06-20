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

import org.jboss.seam.annotations.Filter;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.ContextualHttpServletRequest;

/**
 * Manages the Seam contexts associated with a request to any servlet.
 * 
 * @author Gavin King
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.web.contextFilter")
@Install(value=false, precedence = BUILT_IN)
@Intercept(NEVER)
@Filter(within="org.jboss.seam.web.ajax4jsfFilter")
public class ContextFilter extends AbstractFilter 
{
 
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) 
       throws IOException, ServletException 
   {
      new ContextualHttpServletRequest( (HttpServletRequest) request )
      {
         @Override
         public void process() throws ServletException, IOException
         {
            chain.doFilter(request, response);
         }
      }.run();
   }
}

package org.jboss.seam.wicket.web;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.wicket.Application;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.web.AbstractFilter;
import org.jboss.seam.wicket.WicketManager;

/**
 * Disable the redirect filter when using Wicket (as JSF is an EE library, we
 * can't rely on classDependencies to disable it)
 * 
 * @author 
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.wicket.web.redirectFilter")
@Install(precedence = FRAMEWORK, classDependencies="org.apache.wicket.Application")
@BypassInterceptors
@Filter
public class WicketRedirectFilter extends AbstractFilter 
{

   public void doFilter(ServletRequest request, ServletResponse response,
         FilterChain chain) throws IOException, ServletException 
   {
      chain.doFilter( request, wrapResponse( (HttpServletResponse) response ) );
   }
   
   private static ServletResponse wrapResponse(HttpServletResponse response) 
   {
      return new HttpServletResponseWrapper(response)
      {
         @Override
         public void sendRedirect(String url) throws IOException
         {
            if ( Application.exists() && Contexts.isEventContextActive()) 
            {
               if ( Contexts.isConversationContextActive() )
               {
                  url = WicketManager.instance().appendConversationIdFromRedirectFilter(url);
               }
            }
            super.sendRedirect(url);
         }
      };
   }
}

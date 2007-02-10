package org.jboss.seam.web;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pages;

/**
 * Propagates the conversation context across a browser redirect
 * 
 * @author Gavin King
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.servlet.redirectFilter")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class RedirectFilter extends AbstractFilter 
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
            if ( Contexts.isEventContextActive() )
            {
               String viewId = getViewId(url);
               if (viewId!=null)
               {
                  url = Pages.instance().encodePageParameters( FacesContext.getCurrentInstance(), url, viewId );
               }
               url = Manager.instance().appendConversationIdFromRedirectFilter(url);
            }
            super.sendRedirect(url);
         }
      };
   }
   
   public static String getViewId(String url)
   {
      ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
      String pathInfo = externalContext.getRequestPathInfo();
      String servletPath = externalContext.getRequestServletPath();
      String contextPath = externalContext.getRequestContextPath();
      return getViewId(url, pathInfo, servletPath, contextPath);
   }
   
   protected static String getViewId(String url, String pathInfo, String servletPath, String contextPath)
   {
      if (pathInfo!=null)
      {
         //for /seam/* style servlet mappings
         return url.substring( contextPath.length() + servletPath.length(), getParamLoc(url) );
      }
      else if ( url.startsWith(contextPath) )
      {
         //for *.seam style servlet mappings
         String extension = servletPath.substring( servletPath.lastIndexOf('.') );
         if ( url.endsWith(extension) || url.contains(extension + '?') )
         {
            String suffix = Pages.getSuffix();
            return url.substring( contextPath.length(), getParamLoc(url) - extension.length() ) + suffix;
         }
         else
         {
            return null;
         }
      }
      else
      {
         return null;
      }
   }
   
   private static int getParamLoc(String url)
   {
      int loc = url.indexOf('?');
      if (loc<0) loc = url.length();
      return loc;
   }
}

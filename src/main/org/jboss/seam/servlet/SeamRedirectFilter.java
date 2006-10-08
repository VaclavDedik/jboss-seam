package org.jboss.seam.servlet;

import java.io.IOException;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pages;

/**
 * Propagates the conversation context across a browser redirect
 * 
 * @author Gavin King
 */
public class SeamRedirectFilter implements Filter 
{

   public void init(FilterConfig config) throws ServletException {}

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
                  url = Pages.instance().encodePageParameters(url, viewId);
               }
               url = Manager.instance().appendConversationIdFromRedirectFilter(url);
            }
            super.sendRedirect(url);
         }

      };
   }

   public void destroy() {}  

   public static String getViewId(String url)
   {
      //for /seam/* style servlet mappings
      String pathInfo = FacesContext.getCurrentInstance().getExternalContext().getRequestPathInfo();
      String servletPath = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
      String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
      if (pathInfo!=null)
      {
         return url.substring( contextPath.length() + servletPath.length(), getParamLoc(url) );
      }
      
      //for *.seam style servlet mappings
      if ( url.startsWith(contextPath) )
      {
         String extension = servletPath.substring( servletPath.indexOf('.') );
         if ( url.endsWith(extension) || url.contains(extension + '?') )
         {
            String suffix = getSuffix();
            return url.substring(contextPath.length(), getParamLoc(url) - suffix.length() + 1) + suffix;
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
   
   public static String getSuffix()
   {
      String defaultSuffix = FacesContext.getCurrentInstance().getExternalContext()
            .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
      return defaultSuffix == null ? ViewHandler.DEFAULT_SUFFIX : defaultSuffix;

   }
}

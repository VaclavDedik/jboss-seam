package org.jboss.seam.security.filter;

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Pages;
import org.jboss.seam.pages.Page;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.config.SecurityConfiguration;

/**
 * A servlet filter that checks page security restrictions
 * 
 * @author Shane Bryzak
 */
public class SeamSecurityFilter implements Filter
{
   private SecurityConfiguration config;

   private ServletContext servletContext;

   public void init(FilterConfig filterConfig) throws ServletException
   {
      servletContext = filterConfig.getServletContext();
   }

   /**
    * 
    * @param request ServletRequest
    * @param response ServletResponse
    * @param chain FilterChain
    * @throws IOException
    * @throws ServletException
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
   {
      HttpServletRequest hRequest = (HttpServletRequest) request;
      HttpServletResponse hResponse = (HttpServletResponse) response;

      // Need this so that Pages doesn't throw a NPE
      getFacesContext(request, response);

      try
      {
         Lifecycle.beginRequest(servletContext, hRequest.getSession(), hRequest);
         Identity identity = Identity.instance();
         Page page = Pages.instance().getPage(hRequest.getServletPath());
   
         if (page != null && page.isRestricted())
         {
            try
            {
               String expr = page.getRestriction();
               if (expr == null)
                  expr = String.format("#{s:hasPermission('%s', '%s')}", 
                           page.getViewId(), hRequest.getMethod());
               
               identity.checkRestriction(expr);
   
            }
            catch (AuthorizationException ex)
            {
               hResponse.sendRedirect(String.format("%s%s", hRequest.getContextPath(), config
                        .getSecurityErrorPage()));
               return;
            }
         }
      }
      finally
      {
         Lifecycle.endRequest();
      }         
         
      chain.doFilter(request, response);
   }

   public void destroy()
   {
   }
   
   
   private abstract static class LocalFacesContext extends FacesContext
   {
     protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) 
     {
       FacesContext.setCurrentInstance(facesContext);
     }
   }

   /**
    * Hack to get the FacesContext
    */
   private FacesContext getFacesContext(ServletRequest request, ServletResponse response) 
   {
     FacesContext facesContext = FacesContext.getCurrentInstance();
     if (facesContext != null) return facesContext;

     FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(
              FactoryFinder.FACES_CONTEXT_FACTORY);
     
     LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(
              FactoryFinder.LIFECYCLE_FACTORY);
     
     javax.faces.lifecycle.Lifecycle lifecycle = lifecycleFactory.getLifecycle(
              LifecycleFactory.DEFAULT_LIFECYCLE);

     facesContext = contextFactory.getFacesContext(servletContext, request, response, lifecycle);

     LocalFacesContext.setFacesContextAsCurrentInstance(facesContext);

     return facesContext;
   }   
}

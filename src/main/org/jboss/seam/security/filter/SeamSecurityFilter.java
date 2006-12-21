package org.jboss.seam.security.filter;

import java.io.IOException;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.config.SecurityConfiguration;
import org.jboss.seam.security.config.SecurityConstraint;
import javax.servlet.ServletContext;

/**
 * A servlet filter that performs authentication within a Seam application.
 *
 * @author Shane Bryzak
 */
public class SeamSecurityFilter implements Filter
{
  private SecurityConfiguration config;

  private ServletContext servletContext;

  public void init(FilterConfig filterConfig)
      throws ServletException
  {
    servletContext = filterConfig.getServletContext();
    WebApplicationContext ctx = new WebApplicationContext(servletContext);
    config = (SecurityConfiguration) ctx.get(SecurityConfiguration.class);
  }

  /**
   *
   * @param request ServletRequest
   * @param response ServletResponse
   * @param chain FilterChain
   * @throws IOException
   * @throws ServletException
   */
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain)
      throws IOException, ServletException
  {
    HttpServletRequest hRequest = (HttpServletRequest) request;
    HttpServletResponse hResponse = (HttpServletResponse) response;

    Context sessionContext = new WebSessionContext(
        ContextAdaptor.getSession(hRequest.getSession()));

    Identity ident = (Identity)sessionContext.get(Seam.getComponentName(Identity.class));

    if (!checkSecurityConstraints(hRequest.getServletPath(), hRequest.getMethod(), ident))
      hResponse.sendRedirect(String.format("%s%s", hRequest.getContextPath(),
                                           config.getSecurityErrorPage()));

    chain.doFilter(request, response);
  }

  /**
   * Performs a security check for a specified uri and method, for the specified
   * Identity
   *
   * @param uri String
   * @param method String
   * @param ident Identity
   * @return boolean
   */
  protected boolean checkSecurityConstraints(String uri, String method, Identity ident)
  {
    for (SecurityConstraint c : config.getSecurityConstraints())
    {
      if (c.included(uri, method))
      {
        if (ident == null || !userHasRole(ident, c.getAuthConstraint().getRoles()))
          return false;
      }
    }

    return true;
  }

  /**
   * Returns true if the specified Identity has any one of a number of specified roles.
   *
   * @param ident Identity
   * @param roles String[]
   * @return boolean
   */
  private boolean userHasRole(Identity ident, Set<String> roles)
  {
    for (String role : roles)
    {
      if (ident.isUserInRole(role))
        return true;
    }

    return false;
  }

  public void destroy() {}
}

package org.jboss.seam.security.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationContext;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.authenticator.Authenticator;
import org.jboss.seam.security.config.SecurityConfig;

/**
 * A servlet filter that performs authentication within a Seam application.
 *
 * @author Shane Bryzak
 */
public class SeamSecurityFilter implements Filter
{
  private static final Log log = LogFactory.getLog(SeamSecurityFilter.class);
  private ServletContext servletContext;

//  private static final String CONFIG_RESOURCE = "/WEB-INF/seam-security.xml";

  private AuthenticationContext authContext;
  private Authenticator authenticator;

  public void init(FilterConfig config)
      throws ServletException
  {
    servletContext = config.getServletContext();

    Context appContext = new WebApplicationContext(servletContext);
    SecurityConfig.instance().setApplicationContext(appContext);

    authContext = ((AuthenticationContext) appContext.get(
      "org.jboss.seam.security.AuthenticationContext"));

    authenticator = (Authenticator) appContext.get(
            "org.jboss.seam.security.Authenticator");

    if (authenticator == null)
      throw new ServletException("No Authenticator configured.");

//    try
//    {
//      Lifecycle.setServletContext(servletContext);
//      Lifecycle.beginCall();
//      SecurityConfig.instance().setServletContext(servletContext);
//      SecurityConfig.instance().loadConfig(new SecurityConfigFileLoader(
//        servletContext.getResourceAsStream(CONFIG_RESOURCE), servletContext));
//    }
//    catch (SecurityConfigException ex)
//    {
//      log.error(ex);
//      throw new ServletException("Error loading security configuration", ex);
//    }
//    catch (Exception ex)
//    {
//      throw new ServletException(ex);
//    }
//    finally
//    {
//      Lifecycle.endCall();
//    }
  }

  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain)
      throws IOException, ServletException
  {
//     HttpSession session = ( (HttpServletRequest) request ).getSession(true);

    HttpServletRequest hRequest = (HttpServletRequest) request;
    HttpServletResponse hResponse = (HttpServletResponse) response;

    Context sessionContext = new WebSessionContext(
        ContextAdaptor.getSession(hRequest.getSession()));

    Authentication authentication = (Authentication)sessionContext.get(
            "org.jboss.seam.security.Authentication");


    if (authentication != null)
    {
      try
      {
        authContext.setAuthentication(authenticator.authenticate(authentication));
      }
      catch (AuthenticationException ex)
      {
        authContext.setAuthentication(null);
      }
    }
    else
      authContext.setAuthentication(null);

    try
    {
      chain.doFilter(request, response);
    }
    catch (Exception e)
    {
      if (e instanceof ServletException)
      {
        Throwable cause = ( (ServletException) e).getRootCause();

        Set<Throwable> causes = new HashSet<Throwable> ();
        while (cause != null && !causes.contains(cause))
        {
          if (cause instanceof FailedLoginException)
          {
            // Redirect to login page
            log.info("User not logged in... redirecting to login page.");

//             SecurityConfig.instance().getAuthenticator().showLogin(hRequest, hResponse);
            break;
          }
          causes.add(cause);
          cause = cause.getCause();
        }
      }
      throw new ServletException(e);
    }
    finally
    {
      if (authentication != null)
        authenticator.unauthenticate(authentication);
      authContext.setAuthentication(null);
    }
  }

  public void destroy()
  {}
}

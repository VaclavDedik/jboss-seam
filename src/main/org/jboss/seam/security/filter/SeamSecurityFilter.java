package org.jboss.seam.security.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

  public void init(FilterConfig config)
      throws ServletException
  {
    servletContext = config.getServletContext();

//    try
//    {
      /** @todo beginInitialization is the closest method we have to initialise the application context */
//      Lifecycle.beginInitialization(servletContext);

//      SecurityConfig.instance().setApplicationContext(
//          Contexts.getApplicationContext());

//      if (Authenticator.instance() == null)
//        throw new ServletException("No Authenticator configured.");
//    }
//    finally
//    {
      /** @todo clear the application context */
//    }

//    try
//    {
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
  }

  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain)
      throws IOException, ServletException
  {
    HttpServletRequest hRequest = (HttpServletRequest) request;

//    Context sessionContext = new WebSessionContext(
//        ContextAdaptor.getSession(hRequest.getSession()));
//
//    Authentication authentication = (Authentication)sessionContext.get(
//            "org.jboss.seam.security.Authentication");

//    LoginContext lc = null;
//    try
//    {
//      Lifecycle.beginInitialization(servletContext);
//
//      if (authentication != null)
//      {
//        AuthenticationContext.instance().setAuthentication(Authenticator.instance().authenticate(authentication));
//        CallbackHandler handler = new UsernamePasswordHandler(
//            authentication.getPrincipal().toString(),
//            authentication.getCredentials());
//        try
//        {
//          lc = new LoginContext("client-login", handler);
//          lc.login();
//        }
//        catch (LoginException ex)
//        {
//          ex.printStackTrace();
//        }
//      }
//    }
//    catch (AuthenticationException ex) { }

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
          if (cause instanceof LoginException)
          {
            // Redirect to login page
            log.info("User not logged in... redirecting to login page.");

            /** @todo Redirect based on whatever authentication method is being used */

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
      // Have to set the application context again because it's probably null
//      Lifecycle.beginInitialization(servletContext);

//      AuthenticationContext.instance().setAuthentication(null);

//      if (lc != null)
//      {
//        try
//        {
//          lc.logout();
//        }
//        catch (LoginException ex){ }
//      }

      /** @todo Clear the application context somewhere here */
    }
  }

  public void destroy()
  {}
}

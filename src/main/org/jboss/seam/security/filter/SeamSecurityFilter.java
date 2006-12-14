package org.jboss.seam.security.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.security.config.SecurityConfiguration;
import org.jboss.seam.contexts.WebApplicationContext;

/**
 * A servlet filter that performs authentication within a Seam application.
 *
 * @author Shane Bryzak
 */
public class SeamSecurityFilter implements Filter
{
  private static final Log log = LogFactory.getLog(SeamSecurityFilter.class);

  public void init(FilterConfig config)
      throws ServletException
  {

//    try
//    {
      WebApplicationContext ctx = new WebApplicationContext(config.getServletContext());

      SecurityConfiguration sc = (SecurityConfiguration) ctx.get(
          SecurityConfiguration.class);

      log.info("**** SecurityConfiguration **** : " + sc);

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

//    Context sessionContext = new WebSessionContext(
//        ContextAdaptor.getSession(hRequest.getSession()));
//
//    Authentication authentication = (Authentication)sessionContext.get(
//            "org.jboss.seam.security.Authentication");

//    try
//    {
      chain.doFilter(request, response);
//    }
//    catch (Exception e)
//    {
//      if (e instanceof ServletException)
//      {
//        Throwable cause = ( (ServletException) e).getRootCause();
//
//        Set<Throwable> causes = new HashSet<Throwable> ();
//        while (cause != null && !causes.contains(cause))
//        {
//          if (cause instanceof LoginException)
//          {
            // Redirect to login page
//            log.info("User not logged in... redirecting to login page.");

            /** @todo Redirect based on whatever authentication method is being used */

//             SecurityConfig.instance().getAuthenticator().showLogin(hRequest, hResponse);
//            break;
//          }
//          causes.add(cause);
//          cause = cause.getCause();
//        }
//      }
//      throw new ServletException(e);
//    }
  }

  public void destroy()
  {}
}

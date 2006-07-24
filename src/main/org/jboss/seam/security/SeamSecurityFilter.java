package org.jboss.seam.security;

import javax.servlet.Filter;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import org.jboss.seam.servlet.SeamServletFilter;
import org.jboss.seam.core.Manager;
import javax.servlet.ServletException;
import org.apache.commons.logging.LogFactory;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import org.jboss.seam.contexts.Lifecycle;
import javax.servlet.ServletRequest;
import org.jboss.seam.contexts.ContextAdaptor;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.apache.commons.logging.Log;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import java.net.URL;
import java.net.*;
import java.io.File;
import org.jboss.seam.security.config.SecurityConfig;
import org.jboss.seam.security.config.DefaultSecurityConfigImpl;
import org.jboss.seam.security.config.SecurityConfigException;

/**
 * A servlet filter that performs authentication within a Seam application.
 *
 * @author Shane Bryzak
 */
public class SeamSecurityFilter implements Filter
{
  private static final Log log = LogFactory.getLog(SeamSecurityFilter.class);
  private ServletContext servletContext;

  private SecurityConfig securityConfig;

  private static final String CONFIG_RESOURCE = "/WEB-INF/seam-security.xml";

  public void init(FilterConfig config) throws ServletException {
     servletContext = config.getServletContext();

    try
    {
      securityConfig = new DefaultSecurityConfigImpl(
        servletContext.getResourceAsStream(CONFIG_RESOURCE), servletContext);
    }
    catch (SecurityConfigException ex)
    {
      throw new ServletException("Error loading security configuration", ex);
    }
    catch (Exception ex)
    {
      throw new ServletException(ex);
    }
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException
  {
//     HttpSession session = ( (HttpServletRequest) request ).getSession(true);
//     Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
//     Lifecycle.setServletRequest(request);
//     Lifecycle.beginRequest(servletContext, session, request);
//     Manager.instance().restoreConversation( request.getParameterMap() );
//     Lifecycle.resumeConversation(session);
//     Manager.instance().handleConversationPropagation( request.getParameterMap() );

     try
     {
        chain.doFilter(request, response);

        //TODO: conversation timeout
//        Manager.instance().storeConversation( ContextAdaptor.getSession(session), response );
//       Lifecycle.endRequest(session);
     }
     catch (Exception e)
     {
//        Lifecycle.endRequest();
//        log.error("ended request due to exception", e);
//        throw new ServletException(e);
     }
     finally
     {
//        Lifecycle.setServletRequest(null);
//        Lifecycle.setPhaseId(null);
//        log.debug("ended request");
     }
  }

  public void destroy() {}
}

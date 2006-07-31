package org.jboss.seam.security.filter.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.seam.security.config.SecurityConfig;

/**
 * The authenticator interface
 *
 * @author Shane Bryzak
 */
public interface Handler
{
  void setSecurityConfig(SecurityConfig securityConfig);
  boolean processLogin(HttpServletRequest request, HttpServletResponse response);

  void showLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException;
}

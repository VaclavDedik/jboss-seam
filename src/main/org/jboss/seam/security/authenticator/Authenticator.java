package org.jboss.seam.security.authenticator;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.security.config.SecurityConfig;

/**
 * The authenticator interface
 *
 * @author Shane Bryzak
 */
public interface Authenticator
{
  void setSecurityConfig(SecurityConfig securityConfig);
  boolean processLogin(HttpServletRequest request, HttpServletResponse response);

  void showLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException;
}

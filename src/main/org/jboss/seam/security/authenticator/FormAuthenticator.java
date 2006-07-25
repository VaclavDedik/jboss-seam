package org.jboss.seam.security.authenticator;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of form-based authentication
 *
 * @author Shane Bryzak
 */
public class FormAuthenticator extends BaseAuthenticator
{
  private String loginPage;

  public FormAuthenticator(String loginPage, String errorPage, String defaultPage)
  {
  }

  public void showLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    // Redirect the request to the login page
    response.sendRedirect(response.encodeRedirectURL(request.getContextPath() +
        loginPage));
  }
}

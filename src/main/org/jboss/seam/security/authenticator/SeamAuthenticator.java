package org.jboss.seam.security.authenticator;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 *
 * @author Shane Bryzak
 */
public class SeamAuthenticator extends BaseAuthenticator
{
  private String loginPage;

  public SeamAuthenticator(String loginPage)
  {
    this.loginPage = loginPage;
  }

  public void showLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    // Redirect the request to the login page
    response.sendRedirect(response.encodeRedirectURL(String.format("%s%s",
      request.getContextPath(),loginPage)));
    return;
  }
}

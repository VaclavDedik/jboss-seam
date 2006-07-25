package org.jboss.seam.security.authenticator;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of Basic HTTP authentication
 *
 * @author Shane Bryzak
 */
public class BasicAuthenticator extends BaseAuthenticator
{
  public BasicAuthenticator()
  {
  }

  public void showLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {

  }
}

package org.jboss.seam.example.security;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.UsernamePasswordToken;
import org.jboss.seam.security.provider.AuthenticationProvider;

/**
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Name("authenticatorAction")
public class AuthenticatorAction implements AuthenticationProvider
{
  @In(create=true)
    private EntityManager entityManager;

  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException
  {
    try
    {
      User user = (User) entityManager.createQuery(
          "from User where username = :username and password = :password")
          .setParameter("username", authentication.getPrincipal().toString())
          .setParameter("password", authentication.getCredentials())
          .getSingleResult();

      String[] roles = new String[user.getRoles().size()];
      int idx = 0;
      for (Role role : user.getRoles())
        roles[idx++] = role.getRole();

      UsernamePasswordToken token = new UsernamePasswordToken(
        authentication.getPrincipal(), authentication.getCredentials(), roles);

      return token;
    }
    catch (NoResultException ex)
    {
      throw new AuthenticationException("Invalid username/password");
    }
    catch (Exception ex)
    {
      throw new AuthenticationException("Unknown authentication error", ex);
    }
  }

  public void unauthenticate(Authentication authentication)
  {

  }
}

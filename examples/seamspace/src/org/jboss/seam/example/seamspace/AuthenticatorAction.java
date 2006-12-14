package org.jboss.seam.example.seamspace;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.UsernamePasswordToken;
import org.jboss.seam.security.provider.AuthenticationProvider;
import org.jboss.seam.security.Role;

/**
 * Authenticates the member against the database
 *
 * @author Shane Bryzak
 */
@Name("authenticatorAction")
public class AuthenticatorAction implements AuthenticationProvider
{
  @In(create=true)
    private EntityManager entityManager;

  public Identity authenticate(Identity authentication)
      throws AuthenticationException
  {
    try
    {
      Member member = (Member) entityManager.createQuery(
          "from Member where username = :username and password = :password")
          .setParameter("username", authentication.getPrincipal().toString())
          .setParameter("password", authentication.getCredentials())
          .getSingleResult();

      Role[] roles = new Role[member.getRoles().size()];
      int idx = 0;
      for (MemberRole mr : member.getRoles())
        roles[idx++] = new Role(mr.getName());

      return new UsernamePasswordToken(authentication.getPrincipal(),
                                       authentication.getCredentials(), roles);
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
}

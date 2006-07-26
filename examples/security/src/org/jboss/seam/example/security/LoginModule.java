package org.jboss.seam.example.security;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import javax.persistence.NoResultException;

/**
 *
 * @author Shane Bryzak
 */
@Stateful
@Name("loginModule")
public class LoginModule implements LoginModuleLocal
{
  @PersistenceContext EntityManager manager;

  private String principal;
  private String[] roles;

  public void login(String username, String password)
      throws SecurityException
  {
    try
    {
      User user = (User) manager.createQuery(
          "from User where username = :username and password = :password")
          .setParameter("username", username)
          .setParameter("password", password)
          .getSingleResult();

      principal = user.getUsername();
      roles = new String[user.getRoles().size()];
      int idx = 0;
      for (Role role : user.getRoles())
        roles[idx++] = role.getRole();
    }
    catch (NoResultException ex)
    {
      throw new SecurityException("Invalid username/password");
    }
    catch (Exception ex)
    {
      throw new SecurityException("Unknown error", ex);
    }
  }

  public String getPrincipal()
  {
    return principal;
  }

  public String[] getRoles()
  {
    return roles;
  }

  @Remove @Destroy
  public void destroy()
  {

  }
}

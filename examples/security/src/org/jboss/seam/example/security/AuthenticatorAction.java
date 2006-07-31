package org.jboss.seam.example.security;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.provider.AuthenticationProvider;

/**
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Name("authenticatorAction")
public class AuthenticatorAction implements AuthenticationProvider
{
  @PersistenceContext EntityManager manager;

  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException
  {
    return null;
  }
}

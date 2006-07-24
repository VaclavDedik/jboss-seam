package org.jboss.seam.security.realm;

import java.security.Principal;

/**
 * Allows authentication against a Seam component
 *
 * @author Shane Bryzak
 */
public class SeamRealm implements Realm
{
  public Principal authenticate(String username, String credentials)
  {
    return null;
  }

  public Principal authenticate(String username, byte[] credentials)
  {
    return null;
  }
}

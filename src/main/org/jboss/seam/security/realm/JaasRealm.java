package org.jboss.seam.security.realm;

import java.security.Principal;

/**
 * JAAS realm implementation
 *
 * @author Shane Bryzak
 */
public class JaasRealm implements Realm
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

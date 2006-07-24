package org.jboss.seam.security.realm;

import java.security.Principal;

/**
 * Realm interface
 *
 * @author Shane Bryzak
 */
public interface Realm
{
  Principal authenticate(String username, String credentials);
  Principal authenticate(String username, byte[] credentials);
}

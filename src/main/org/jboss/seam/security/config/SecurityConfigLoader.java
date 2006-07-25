package org.jboss.seam.security.config;

import java.util.Set;

import org.jboss.seam.security.authenticator.Authenticator;
import org.jboss.seam.security.realm.Realm;

/**
 * <p> </p>
 *
 * @author Shane Bryzak
 */
public interface SecurityConfigLoader
{
  Set<SecurityConstraint> getSecurityConstraints();
  AuthMethod getAuthMethod();
  Authenticator getAuthenticator();
  Set<String> getSecurityRoles();
  Realm getRealm();
}

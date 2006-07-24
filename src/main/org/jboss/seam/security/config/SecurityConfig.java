package org.jboss.seam.security.config;

import java.util.Set;

import org.jboss.seam.security.authenticator.Authenticator;
import org.jboss.seam.security.realm.Realm;

/**
 * Security Configuration interface.
 *
 * @author Shane Bryzak
 */
public interface SecurityConfig
{
  Set<SecurityConstraint> getSecurityConstraints();
  AuthMethod getAuthMethod();
  Authenticator getAuthenticator();
  Realm getRealm();
}

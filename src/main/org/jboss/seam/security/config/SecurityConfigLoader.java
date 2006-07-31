package org.jboss.seam.security.config;

import java.util.Set;

import org.jboss.seam.security.filter.handler.Handler;

/**
 * <p> </p>
 *
 * @author Shane Bryzak
 */
public interface SecurityConfigLoader
{
  Set<SecurityConstraint> getSecurityConstraints();
  AuthMethod getAuthMethod();
  Handler getAuthenticator();
  Set<String> getSecurityRoles();
}

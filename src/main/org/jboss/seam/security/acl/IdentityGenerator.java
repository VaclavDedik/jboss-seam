package org.jboss.seam.security.acl;

/**
 * Classes implementing this interface generate ACL Identities
 *
 * @author Shane Bryzak
 */
public interface IdentityGenerator
{
  String generateIdentity(Object obj);
}

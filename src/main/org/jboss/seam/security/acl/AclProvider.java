package org.jboss.seam.security.acl;

import java.security.acl.Acl;

import org.jboss.seam.security.Authentication;

/**
 * Provides a list of Acls for an object.
 *
 * @author Shane Bryzak
 */
public interface AclProvider
{
  /**
   * Return all Acls for the specified object.
   *
   * @param value Object
   * @return Permissions
   */
  Acl getAcls(Object value);

  /**
   * Return all Acls for the specified object that apply to the specified Authentication.
   *
   * @param value Object
   * @param auth Authentication
   * @return Permissions
   */
  Acl getAcls(Object value, Authentication auth);
}

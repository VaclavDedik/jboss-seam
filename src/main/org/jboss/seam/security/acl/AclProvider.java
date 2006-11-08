package org.jboss.seam.security.acl;

import java.security.Principal;
import java.security.acl.Acl;

/**
 * Provides a list of Acls for an object.
 *
 * @author Shane Bryzak
 */
public interface AclProvider
{
  public enum RecipientType {role, user};

  /**
   * Return all Acls for the specified object.
   *
   * @param value Object
   * @return Acl
   */
  Acl getAcls(Object value);

  /**
   * Return all Acls for the specified object that apply to the specified Principal.
   *
   * @param value Object
   * @param principal Principal
   * @return Acl
   */
  Acl getAcls(Object value, Principal principal);
}

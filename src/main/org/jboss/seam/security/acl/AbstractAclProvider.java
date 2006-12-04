package org.jboss.seam.security.acl;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.security.Authentication;

/**
 * Abstract base implementation of AclProvider
 *
 * @author Shane Bryzak
 */
public abstract class AbstractAclProvider implements AclManager
{
  public Acl getAcls(Object obj)
  {
    return internalGetAcls(obj, null);
  }

  public Acl getAcls(Object obj, Principal principal)
  {
    if (principal == null)
      throw new IllegalArgumentException("Principal cannot be null");

    return internalGetAcls(obj, principal);
  }

  protected Acl internalGetAcls(Object obj, Principal principal)
  {
    Principal owner = Authentication.instance();

    Acl acl = new AclImpl(owner);

    AclEntry entry = new AclEntryImpl();

    if (principal != null)
    {
      entry.setPrincipal(principal);

      for (Permission p : getPermissions(obj, principal))
      {
        entry.addPermission(p);
      }
    }

    try
    {
      acl.addEntry(owner, entry);
    }
    catch (NotOwnerException ex) { } // caller is owner

    return acl;
  }

  protected abstract Set<Permission> getPermissions(Object obj, Principal principal);
  protected abstract Map<Principal,Set<Permission>> getPermissions(Object obj);
}

package org.jboss.seam.security.acl;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

/**
 * Provides a list of Acls for an object.
 *
 * @author Shane Bryzak
 */
//@Name("org.jboss.seam.security.aclProvider")
public abstract class AclProvider
{
  public enum RecipientType {role, user};

  /**
   * Return all Acls for the specified object.
   *
   * @param value Object
   * @return Acl
   */
  public Acl getAcls(Object obj)
  {
    return internalGetAcls(obj, null);
  }

  /**
   * Return all Acls for the specified object that apply to the specified Principal.
   *
   * @param value Object
   * @param principal Principal
   * @return Acl
   */
  public Acl getAcls(Object obj, Principal principal)
  {
    if (principal == null)
      throw new IllegalArgumentException("Principal cannot be null");

    return internalGetAcls(obj, principal);
  }

  protected Acl internalGetAcls(Object obj, Principal principal)
  {
    Principal owner = Identity.instance().getPrincipal();

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


  protected Set<Permission> convertToPermissions(Principal principal, Object target, Object perms)
  {
    if (perms == null)
      return null;

    //SeamSecurityManager.instance().get

        if (List.class.isAssignableFrom(perms.getClass()))
        {
      Set<Permission> permissions = new HashSet<Permission>();

      for (Object o : (List) perms)
      {
        if (o instanceof Object[])
        {
          Object[] values = (Object[]) o;
          int mask = (Integer) values[0];
          String recipient = (String) values[1];
          RecipientType recipientType = (RecipientType) values[2];

//          DefinePermissions def = target.getClass().getAnnotation(DefinePermissions.class);
//          for (org.jboss.seam.annotations.security.AclProvider provider : def.permissions())
//          {
//            if ((provider.mask() & mask) > 0)
              /** todo - use the correct name to create the permission */
//              permissions.add(new SeamPermission("permissionName", provider.action()));
//          }
        }
      }

      return permissions;
        }
    else
      throw new IllegalArgumentException(String.format(
          "Permissions [%s] must be an instance of java.util.List", perms));
  }

  protected abstract Set<Permission> getPermissions(Object obj, Principal principal);
  protected abstract Map<Principal,Set<Permission>> getPermissions(Object obj);
}

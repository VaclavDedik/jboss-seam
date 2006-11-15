package org.jboss.seam.security;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.Permission;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.security.DefinePermissions;
import org.jboss.seam.security.acl.AclProvider;

/**
 * 
 * @author shane_bryzak
 *
 */
public class PermissionHandler 
{
  
  private String permissionName;
  private Map<String,String> providers = new HashMap<String,String>();
  
  public PermissionHandler(Class cls)
  {
    DefinePermissions def = null;
   
    if (cls.isAnnotationPresent(DefinePermissions.class))
      def = (DefinePermissions) cls.getAnnotation(DefinePermissions.class);

    // Determine the permission name.  If it is specified in a @DefinePermissions
    // annotation, use that one, otherwise use the component name.  If the object
    // is not a Seam component, use its fully qualified class name.
    if (def != null && !"".equals(def.name()))
    {
      permissionName = ((DefinePermissions) cls.getAnnotation(DefinePermissions.class)).name();
    }
    else
      permissionName = Seam.getComponentName(cls);

    if (permissionName == null)
      permissionName = cls.getName();

    if (def != null)
    {
      for (org.jboss.seam.annotations.security.AclProvider p : def.permissions())
      {
        providers.put(p.action(), p.provider());        
      }
    }
  }
  
  public String getPermissionName()
  {
    return permissionName;
  }

  public String getProviderName(String action)
  {
    return providers.get(action);
  }

  public boolean supportsAclCheck(String action)
  {
    return providers.containsKey(action);
  }  
  
  /**
   * Performs an ACL permission check against the currently authenticated principal.
   *
   * A SecurityException is thrown if the currently authenticated user does not
   * have the necessary permission for the specified object.
   *
   * @param obj Object The object to be checked
   * @param action String The action
   */
  public void aclCheck(Object obj, String action)
  {
    Permission required = new SeamPermission(permissionName, action);

    AclProvider provider = (AclProvider) Component.getInstance(providers.get(action), true);
    Principal principal = Authentication.instance();
    
    if (provider != null)
    {
      Acl acl = provider.getAcls(obj, principal);
      if (acl != null && acl.checkPermission(principal, required))
        return;
    }
    else
      throw new IllegalArgumentException("Invalid action specified - no ACL provider found");
    
    throw new SecurityException(String.format(
        "Principal %s failed permission check %s on object [%s].",
        principal, required, obj));
  }  
}

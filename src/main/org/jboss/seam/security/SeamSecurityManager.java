package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.security.Permissions;
import java.security.acl.Permission;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.acl.AclProvider;
import org.jboss.seam.security.acl.IdentityGenerator;
import org.jboss.seam.security.acl.JPAIdentityGenerator;

/**
 * Holds configuration settings and provides functionality for the security API
 *
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.securityManager")
@Install(precedence=BUILT_IN)
@Intercept(InterceptionType.NEVER)
public class SeamSecurityManager
{
  /**
   * An action code that directs the user to a login page.
   */
  private String loginAction = "login";

  /**
   * An action code that directs the user to a security error page.
   */
  private String securityErrorAction = "securityError";

  private IdentityGenerator identityGenerator = new JPAIdentityGenerator();

  /**
   * Map roles to permissions
   */
  private Map<String,Set<Permission>> rolePermissions = new HashMap<String,Set<Permission>>();

  /**
   *
   */
  private Map<Class,PermissionHandler> permissionHandlers = new HashMap<Class,PermissionHandler>();

  public static SeamSecurityManager instance()
  {
    if (!Contexts.isApplicationContextActive())
       throw new IllegalStateException("No active application context");

    SeamSecurityManager instance = (SeamSecurityManager) Component.getInstance(
        SeamSecurityManager.class, ScopeType.APPLICATION);

    if (instance==null)
    {
      throw new IllegalStateException(
          "No SeamSecurityManager could be created, make sure the Component exists in application scope");
    }

    return instance;
  }

  public String getLoginAction()
  {
    return loginAction;
  }

  public void setLoginAction(String loginAction)
  {
    this.loginAction = loginAction;
  }

  public String getSecurityErrorAction()
  {
    return securityErrorAction;
  }

  public IdentityGenerator getIdentityGenerator()
  {
    return identityGenerator;
  }

  public void setIdentityGenerator(IdentityGenerator identityGenerator)
  {
    this.identityGenerator = identityGenerator;
  }

  public void setSecurityErrorAction(String securityErrorAction)
  {
    this.securityErrorAction = securityErrorAction;
  }

  public void checkPermission(String permissionName, String action)
  {
    checkRolePermissions(permissionName, action);
  }

  public void checkPermission(Object obj, String action)
  {
    PermissionHandler handler = getPermissionHandler(obj.getClass());

    if (handler.supportsAclCheck(action))
      handler.aclCheck(obj, action);
    else
      checkRolePermissions(handler.getPermissionName(), action);
  }

  /**
   *
   * @param permissionName
   * @param action
   */
  private void checkRolePermissions(String permissionName, String action)
  {
    Permission required = new SeamPermission(permissionName, action);
    for (String role : Identity.instance().getRoles())
    {
      Set<Permission> permissions = rolePermissions.get(role);
      if (permissions != null && permissions.contains(required))
        return;
    }
  }

  protected PermissionHandler getPermissionHandler(Class cls)
  {
    if (!permissionHandlers.containsKey(cls))
    {
      synchronized(permissionHandlers)
      {
        if (!permissionHandlers.containsKey(cls))
        {
          PermissionHandler handler = new PermissionHandler(cls);
          permissionHandlers.put(cls, handler);
          return handler;
        }
      }
    }

    return permissionHandlers.get(cls);
  }

  public Permissions getPermissions(Object value)
  {
    return null;
  }

  public Permissions getPermissions(Object value, Identity ident)
  {
    return null;
  }

  public String getObjectIdentity(Object obj)
  {
    return identityGenerator.generateIdentity(obj);
  }

  public void grantPermission(Object target, String action, String recipient,
                               AclProvider.RecipientType recipientType)
  {
    /** @todo  */
  }

  public void revokePermission(Object target, String action, String recipient,
                               AclProvider.RecipientType recipientType)
  {
    /** @todo  */
  }
}

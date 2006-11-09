package org.jboss.seam.security;

import java.security.Permissions;
import java.security.acl.Acl;
import java.security.acl.Permission;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.DefinePermissions;
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

  private class PermissionsMetadata {
    private String name;
    private Map<String,String> providerNames = new HashMap<String,String>();

    public PermissionsMetadata(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public String getProviderName(String action)
    {
      return providerNames.get(action);
    }

    public void addProviderName(String action, String providerName)
    {
      providerNames.put(action, providerName);
    }
  }

  /**
   *
   */
  private Map<Class,PermissionsMetadata> classPermissions = new HashMap<Class,PermissionsMetadata>();

  public static SeamSecurityManager instance()
  {
    if (!Contexts.isApplicationContextActive())
       throw new IllegalStateException("No active application context");

    SeamSecurityManager instance = (SeamSecurityManager) Component.getInstance(
        SeamSecurityManager.class, ScopeType.APPLICATION, true);

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

  public void checkPermission(String name, String action)
  {
    checkPermission(name, action, null, null);
  }

  public void checkPermission(Object obj, String action)
  {
    PermissionsMetadata meta = getClassPermissionMetadata(obj.getClass());

    String providerName = meta.getProviderName(action);
    Object provider = null;

    if (providerName != null && !"".equals(providerName))
      provider = Component.getInstance(providerName, true);

    if (!AclProvider.class.isAssignableFrom(provider.getClass()))
      throw new IllegalStateException(String.format(
        "Provider [%s] not instance of AclProvider", provider.toString()));

    checkPermission(meta.getName(), action, obj, (AclProvider) provider);
  }

  /**
   * Checks the permission specified by name and action for an object.  If an
   * AclProvider is specified, then only an ACL check will be carried out using
   * the provider.  Otherwise, the permissions implied by the roles held by the
   * currently authenticated user will be checked.
   *
   * A SecurityException is thrown if the currently authenticated user does not
   * have the necessary permission for the specified object.
   *
   * @param name String The name of the permission
   * @param action String The action
   * @param obj Object The object to be checked
   * @param aclProvider AclProvider ACL Provider for the specified object, or null if no provider
   */
  private void checkPermission(String name, String action, Object obj, AclProvider aclProvider)
  {
    Permission required = new SeamPermission(name, action);

    if (aclProvider != null)
    {
      Acl acl = aclProvider.getAcls(obj, Authentication.instance());
      if (acl != null && acl.checkPermission(Authentication.instance(), required))
        return;
    }
    else
    {
      for (String role : Authentication.instance().getRoles())
      {
        Set<Permission> permissions = rolePermissions.get(role);
        if (permissions != null && permissions.contains(required))
          return;
      }
    }

    throw new SecurityException(String.format(
      "Authenticated principal does not contain required permission %s",
      required));
  }

  private PermissionsMetadata getClassPermissionMetadata(Class cls)
  {
    if (!classPermissions.containsKey(cls))
    {
      synchronized(classPermissions)
      {
        if (!classPermissions.containsKey(cls))
        {
          // Determine the permission name.  If it is specified in a @DefinePermissions
          // annotation, use that one, otherwise use the component name.  If the object
          // is not a Seam component, use its fully qualified class name.

          String name = null;

          DefinePermissions def = null;

          if (cls.isAnnotationPresent(DefinePermissions.class))
            def = (DefinePermissions) cls.getAnnotation(DefinePermissions.class);

          if (def != null && !"".equals(def.name()))
          {
            name = ((DefinePermissions) cls.getAnnotation(DefinePermissions.class)).name();
          }
          else
            name = Seam.getComponentName(cls);

          if (name == null)
            name = cls.getName();

          PermissionsMetadata meta = new PermissionsMetadata(name);

          if (def != null)
          {
            for (org.jboss.seam.annotations.security.AclProvider p : def.permissions())
            {
              meta.addProviderName(p.action(), p.provider());
            }
          }

          classPermissions.put(cls, meta);
          return meta;
        }
      }
    }

    return classPermissions.get(cls);
  }

  public Permissions getPermissions(Object value)
  {
    return null;
  }

  public Permissions getPermissions(Object value, Authentication auth)
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

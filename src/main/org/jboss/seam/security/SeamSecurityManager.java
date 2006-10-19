package org.jboss.seam.security;

import java.security.Permissions;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.DefinePermissions;
import org.jboss.seam.annotations.AclProvider;

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

  /**
   * Maps roles to permissions
   */
  private Map<String,Set<SeamPermission>> rolePermissions = new HashMap<String,Set<SeamPermission>>();

  private class PermissionsMetadata {
    private String name;
    private Map<String,String> providerNames;

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

    checkPermission(meta.getName(), action, obj, provider);
  }

  private void checkPermission(String name, String action, Object obj, Object aclProvider)
  {
    for (String role : Authentication.instance().getRoles())
    {
      Set<SeamPermission> permissions = rolePermissions.get(role);
      if (permissions != null)
      {
        for (SeamPermission p : permissions)
        {
          if (p.getName().equals(name) && p.containsAction(action))
          {
            if (aclProvider == null)
              return;


          }
        }
      }
    }

    throw new SecurityException(String.format(
      "Authenticated principal does not contain required permission [name=%s,action=%s]",
      name, action));
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
            for (AclProvider p : def.permissions())
            {
              for (String action : p.actions().split("[,]"))
              {
                meta.addProviderName(action.trim(), p.provider());
              }
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
}

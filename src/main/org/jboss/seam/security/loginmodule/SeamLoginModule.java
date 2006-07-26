package org.jboss.seam.security.loginmodule;

import java.lang.reflect.Method;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.spi.LoginModule;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.security.config.SecurityConfig;
import org.jboss.seam.security.realm.RolePrincipal;
import org.jboss.seam.security.realm.UserPrincipal;

/**
 * A LoginModule that provides authentication against a Seam component.
 *
 * @author Shane Bryzak
 */
public class SeamLoginModule implements LoginModule
{
  private static final String CONFIG_COMPONENT_NAME = "component-name";
  private static final String CONFIG_LOGIN_METHOD = "login-method";
  private static final String CONFIG_PRINCIPAL_METHOD = "principal-method";
  private static final String CONFIG_ROLES_METHOD = "roles-method";

  private static final String DEFAULT_COMPONENT_NAME = "loginModule";
  private static final String DEFAULT_LOGIN_METHOD = "login";
  private static final String DEFAULT_PRINCIPAL_METHOD = "getPrincipal";
  private static final String DEFAULT_ROLES_METHOD = "getRoles";

  private Subject subject;
  private CallbackHandler callbackHandler;

  private String componentName;
  private String loginMethodName;
  private String principalMethodName;
  private String rolesMethodName;

  private String principal;
  private String[] roles;

  public boolean abort()
  {
    principal = null;
    roles = null;
    return true;
  }

  public boolean commit()
  {
    subject.getPrincipals().add(new UserPrincipal(principal));
    for (String role : roles)
      subject.getPrincipals().add(new RolePrincipal(principal));
    return true;
  }

  public void initialize(Subject subject, CallbackHandler handler,
                         Map<String,?> sharedState, Map<String,?> options)
  {
    this.subject = subject;
    this.callbackHandler = handler;

    componentName = options.containsKey(CONFIG_COMPONENT_NAME) ?
        (String) options.get(CONFIG_COMPONENT_NAME) : DEFAULT_COMPONENT_NAME;
    loginMethodName = options.containsKey(CONFIG_LOGIN_METHOD) ?
        (String) options.get(CONFIG_LOGIN_METHOD) : DEFAULT_LOGIN_METHOD;
    principalMethodName = options.containsKey(CONFIG_PRINCIPAL_METHOD) ?
        (String) options.get(CONFIG_PRINCIPAL_METHOD) : DEFAULT_PRINCIPAL_METHOD;
    rolesMethodName = options.containsKey(CONFIG_ROLES_METHOD) ?
        (String) options.get(CONFIG_ROLES_METHOD) : DEFAULT_ROLES_METHOD;
  }

  public boolean login()
  {
    try
    {
//      Lifecycle.setServletContext(SecurityConfig.instance().getServletContext());
//      Lifecycle.beginCall();

      Object obj = Component.getInstance(componentName, true);
      Method loginMethod = obj.getClass().getMethod(loginMethodName, String.class, String.class);
      Method principalMethod = obj.getClass().getMethod(principalMethodName);
      Method rolesMethod = obj.getClass().getMethod(rolesMethodName);

      NameCallback nameCallback = new NameCallback("Username");
      PasswordCallback pwCallback = new PasswordCallback("Password", false);
      callbackHandler.handle(new Callback[]{nameCallback, pwCallback });

      loginMethod.invoke(obj, nameCallback.getName(), new String(pwCallback.getPassword()));

      principal = (String) principalMethod.invoke(obj);
      roles = (String[]) rolesMethod.invoke(obj);

      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
    finally
    {
//      Lifecycle.endCall();
    }
  }

  public boolean logout()
  {
    principal = null;
    roles = null;
    return true;
  }
}

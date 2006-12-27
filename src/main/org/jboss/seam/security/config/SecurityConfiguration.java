package org.jboss.seam.security.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.InterceptionType;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.SeamPermission;
import org.jboss.seam.util.Resources;

/**
 * Security configuration component.
 *
 * @author Shane Bryzak
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.security.securityConfiguration")
@Install(value = false, precedence=BUILT_IN, dependencies = "org.jboss.seam.securityManager")
@Intercept(InterceptionType.NEVER)
public class SecurityConfiguration
{
  private static final String SECURITY_CONFIG_FILENAME = "/META-INF/security-config.xml";

  private static final LogProvider log = Logging.getLogProvider(SecurityConfiguration.class);

  // <security-constraint>
  private static final String SECURITY_CONSTRAINT = "security-constraint";
  private static final String WEB_RESOURCE_COLLECTION = "web-resource-collection";
  private static final String URL_PATTERN = "url-pattern";
  private static final String HTTP_METHOD = "http-method";
  private static final String AUTH_CONSTRAINT = "auth-constraint";
  private static final String ROLE_NAME = "role-name";

  // <login-config>
//  private static final String LOGIN_CONFIG = "login-config";
//  private static final String AUTH_METHOD = "auth-method";

  // FORM
//  private static final String FORM_LOGIN_CONFIG = "form-login-config";
//  private static final String FORM_LOGIN_PAGE = "form-login-page";
//  private static final String FORM_ERROR_PAGE = "form-error-page";
//  private static final String FORM_DEFAULT_PAGE = "form-default-page";

  // roles
  private static final String SECURITY_ROLES = "roles";
  private static final String SECURITY_ROLE = "role";
  private static final String SECURITY_MEMBERSHIPS = "memberships";
  private static final String SECURITY_PERMISSIONS = "permissions";
  private static final String SECURITY_PERMISSION = "permission";

  private Set<SecurityConstraint> securityConstraints = new HashSet<SecurityConstraint>();

  private Map<String,Role> securityRoles = new HashMap<String,Role>();

  private String securityErrorPage = "/securityError.seam";

//  private AuthMethod authMethod;

//  private Handler authenticator;

  /**
   * Initialization
   *
   * @throws SecurityConfigException
   */
  @Create
  public void init()
      throws SecurityConfigException
  {
    InputStream in = Resources.getResourceAsStream(SECURITY_CONFIG_FILENAME);
    if (in != null)
      loadConfigFromStream(in);
    else
      log.warn(String.format("Security configuration file %s not found", SECURITY_CONFIG_FILENAME));
  }

  public void setSecurityErrorPage(String securityErrorPage)
  {
    this.securityErrorPage = securityErrorPage;
  }

  public String getSecurityErrorPage()
  {
    return securityErrorPage;
  }

  /**
   * Loads the security configuration from the specified InputStream.
   *
   * @param config InputStream
   * @throws SecurityConfigException
   */
  protected void loadConfigFromStream(InputStream config)
      throws SecurityConfigException
  {
    try
    {
      // Parse the incoming request as XML
      SAXReader xmlReader = new SAXReader();
      Document doc = xmlReader.read(config);
      Element env = doc.getRootElement();

      loadSecurityConstraints(env.elements(SECURITY_CONSTRAINT));
      loadSecurityRoles(env.element(SECURITY_ROLES));

      //      loadLoginConfig(env.element(LOGIN_CONFIG));
    }
    catch (Exception ex)
    {
      if (ex instanceof SecurityConfigException)
        throw (SecurityConfigException) ex;
      else
        throw new SecurityConfigException("Error loading security configuration", ex);
    }
  }

  /**
   * Returns the configured security constraints
   *
   * @return Set
   */
  public Set<SecurityConstraint> getSecurityConstraints()
  {
    return securityConstraints;
  }

  /**
   *
   * @return AuthMethod
   */
//  public AuthMethod getAuthMethod()
//  {
//    return authMethod;
//  }

  /**
   *
   * @return Authenticator
   */
//  public Handler getAuthenticator()
//  {
//    return authenticator;
//  }

  /**
   *
   * @return Set
   */
  public Set<Role> getSecurityRoles()
  {
    return new HashSet<Role>(securityRoles.values());
  }

  /**
   * Load security constraints
   *
   * @param elements List
   * @throws SecurityConfigurationException
   */
  protected void loadSecurityConstraints(List elements)
      throws SecurityConfigException
  {
    try
    {
      for (Element element : (List<Element>) elements)
      {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        securityConstraints.add(securityConstraint);

        for (Element wrcElement :
            (List<Element>) element.elements(WEB_RESOURCE_COLLECTION))
        {
          WebResourceCollection wrc = new WebResourceCollection();
          securityConstraint.getResourceCollections().add(wrc);

          for (Element urlPatternElement :
              (List<Element>) wrcElement.elements(URL_PATTERN))
          {
            wrc.getUrlPatterns().add(urlPatternElement.getTextTrim());
          }

          for (Element httpMethodElement :
              (List<Element>) wrcElement.elements(HTTP_METHOD))
          {
            wrc.getHttpMethods().add(httpMethodElement.getTextTrim());
          }
        }

        securityConstraint.setAuthConstraint(new AuthConstraint());
        for (Element roleNameElement :
            (List<Element>) element.element(AUTH_CONSTRAINT).elements(ROLE_NAME))
        {
          securityConstraint.getAuthConstraint().getRoles().add(roleNameElement.
              getTextTrim());
        }
      }
    }
    catch (Exception ex)
    {
      throw new SecurityConfigException("Error loading security constraints", ex);
    }
  }

  /**
   * Load login configuration
   *
   * @param loginConfigElement Element
   * @throws SecurityConfigurationException
   */
//  private void loadLoginConfig(Element loginConfigElement)
//      throws SecurityConfigException
//  {
//    String authMethodText = loginConfigElement.element(AUTH_METHOD).getTextTrim();
//    try
//    {
//      authMethod = AuthMethod.valueOf(authMethodText);
//    }
//    catch (Exception ex)
//    {
//      StringBuilder sb = new StringBuilder();
//      for (AuthMethod m : AuthMethod.values())
//      {
//        if (sb.length() > 0)
//          sb.append(',');
//        sb.append(m.toString());
//      }
//
//      throw new SecurityConfigException(
//          String.format("Invalid auth-method [%s].  Valid options are: %s",
//                        authMethodText, sb.toString()));
//    }
//
//    switch (authMethod)
//    {
//      case BASIC:
//        authenticator = new BasicHandler();
//        break;
//      case FORM:
//        Element formConfigElement = loginConfigElement.element(FORM_LOGIN_CONFIG);
//        String loginPage = formConfigElement.elementText(FORM_LOGIN_PAGE);
//        String errorPage = formConfigElement.elementText(FORM_ERROR_PAGE);
//        String defaultPage = formConfigElement.elementText(FORM_DEFAULT_PAGE);
//        authenticator = new FormHandler(loginPage, errorPage, defaultPage);
//        break;
//      case SEAM:
//        Element seamConfigElement = loginConfigElement.element(SEAM_LOGIN_CONFIG);
//        loginPage = seamConfigElement.elementText(SEAM_LOGIN_PAGE);
//        authenticator = new SeamAuthenticator(loginPage);
//        break;
//    }
//
//    if (authenticator == null)
//      throw new SecurityConfigException(
//        String.format("No valid authenticator for auth-method [%s]", authMethod.toString()));
//
//  }

  /**
   * Load the security roles
   *
   * @param securityRoleElement Element
   * @throws SecurityConfigurationException
   */
  protected void loadSecurityRoles(Element securityRoleElement)
      throws SecurityConfigException
  {
    Map<String,Set<String>> members = new HashMap<String,Set<String>>();

    for (Element role : (List<Element>) securityRoleElement.elements(SECURITY_ROLE))
    {
      Role r = new Role(role.attributeValue("name"));

      Set<String> mbrs = new HashSet<String>();
      members.put(r.getName(), mbrs);

      Element m = role.element(SECURITY_MEMBERSHIPS);
      if (m != null)
      {
        for (String member : m.getTextTrim().split("[,]"))
          mbrs.add(member);
      }

      Element permissionsElement = role.element(SECURITY_PERMISSIONS);
      if (permissionsElement != null)
      {
        for (Element permission : (List<Element>) permissionsElement.elements(
            SECURITY_PERMISSION))
        {
          r.addPermission(new SeamPermission(permission.attributeValue("name"),
                                             permission.attributeValue("action")));
        }
      }

      securityRoles.put(r.getName(), r);
    }

    for (String roleName : members.keySet())
    {
      Role r = securityRoles.get(roleName);
      for (String member : members.get(roleName))
        r.addMember(securityRoles.get(member));
    }
  }

}

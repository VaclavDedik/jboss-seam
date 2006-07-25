package org.jboss.seam.security.config;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.security.authenticator.Authenticator;
import org.jboss.seam.security.authenticator.BasicAuthenticator;
import org.jboss.seam.security.authenticator.FormAuthenticator;
import org.jboss.seam.security.authenticator.SeamAuthenticator;
import org.jboss.seam.security.realm.Realm;

/**
 * Loads the security configuration from an XML configuration file.
 *
 * @author Shane Bryzak
 */
public class SecurityConfigFileLoader implements SecurityConfigLoader
{
  // <security-constraint>
  private static final String SECURITY_CONSTRAINT = "security-constraint";
  private static final String WEB_RESOURCE_COLLECTION = "web-resource-collection";
  private static final String URL_PATTERN = "url-pattern";
  private static final String HTTP_METHOD = "http-method";
  private static final String AUTH_CONSTRAINT = "auth-constraint";
  private static final String ROLE_NAME = "role-name";

  // <login-config>
  private static final String LOGIN_CONFIG = "login-config";
  private static final String AUTH_METHOD = "auth-method";

  // FORM
  private static final String FORM_LOGIN_CONFIG = "form-login-config";
  private static final String FORM_LOGIN_PAGE = "form-login-page";
  private static final String FORM_ERROR_PAGE = "form-error-page";
  private static final String FORM_DEFAULT_PAGE = "form-default-page";

  // SEAM
  private static final String SEAM_LOGIN_CONFIG = "seam-login-config";
  private static final String SEAM_LOGIN_PAGE = "seam-login-page";

  // <security-role>
  private static final String SECURITY_ROLE = "security-role";

  // <realm>
  private static final String REALM = "realm";
  private static final String CLASSNAME_ATTRIBUTE = "className";

  private Set<SecurityConstraint> securityConstraints = new HashSet<SecurityConstraint>();

  private Set<String> securityRoles = new HashSet<String>();

  private AuthMethod authMethod;

  private Authenticator authenticator;

  private ServletContext servletContext;

  private Realm realm;

  /**
   * Constructor, loads the configuration from configFile.
   *
   * @param configFile File
   */
  public SecurityConfigFileLoader(InputStream config, ServletContext servletContext)
      throws SecurityConfigException
  {
    try
    {
      this.servletContext = servletContext;

      // Parse the incoming request as XML
      SAXReader xmlReader = new SAXReader();
      Document doc = xmlReader.read(config);
      Element env = doc.getRootElement();

      loadSecurityConstraints(env.elements(SECURITY_CONSTRAINT));
      loadLoginConfig(env.element(LOGIN_CONFIG));
      loadSecurityRoles(env.element(SECURITY_ROLE));
      loadRealm(env.element(REALM));
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
  public AuthMethod getAuthMethod()
  {
    return authMethod;
  }

  /**
   *
   * @return Authenticator
   */
  public Authenticator getAuthenticator()
  {
    return authenticator;
  }

  /**
   *
   * @return Set
   */
  public Set<String> getSecurityRoles()
  {
    return securityRoles;
  }

  /**
   *
   * @return Realm
   */
  public Realm getRealm()
  {
    return realm;
  }

  /**
   * Load security constraints
   *
   * @param elements List
   * @throws SecurityConfigurationException
   */
  private void loadSecurityConstraints(List elements)
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
  private void loadLoginConfig(Element loginConfigElement)
      throws SecurityConfigException
  {
    String authMethodText = loginConfigElement.element(AUTH_METHOD).getTextTrim();
    try
    {
      authMethod = AuthMethod.valueOf(authMethodText);
    }
    catch (Exception ex)
    {
      StringBuilder sb = new StringBuilder();
      for (AuthMethod m : AuthMethod.values())
      {
        if (sb.length() > 0)
          sb.append(',');
        sb.append(m.toString());
      }

      throw new SecurityConfigException(
          String.format("Invalid auth-method [%s].  Valid options are: %s",
                        authMethodText, sb.toString()));
    }

    switch (authMethod)
    {
      case BASIC:
        authenticator = new BasicAuthenticator();
        break;
      case FORM:
        Element formConfigElement = loginConfigElement.element(FORM_LOGIN_CONFIG);
        String loginPage = formConfigElement.elementText(FORM_LOGIN_PAGE);
        String errorPage = formConfigElement.elementText(FORM_ERROR_PAGE);
        String defaultPage = formConfigElement.elementText(FORM_DEFAULT_PAGE);
        authenticator = new FormAuthenticator(loginPage, errorPage, defaultPage);
        break;
      case SEAM:
        Element seamConfigElement = loginConfigElement.element(SEAM_LOGIN_CONFIG);
        loginPage = seamConfigElement.elementText(SEAM_LOGIN_PAGE);
        authenticator = new SeamAuthenticator(loginPage);
        break;
    }

    if (authenticator == null)
      throw new SecurityConfigException(
        String.format("No valid authenticator for auth-method [%s]", authMethod.toString()));

  }

  /**
   * Load the security roles
   *
   * @param securityRoleElement Element
   * @throws SecurityConfigurationException
   */
  private void loadSecurityRoles(Element securityRoleElement)
      throws SecurityConfigException
  {
    for (Element roleName : (List<Element>) securityRoleElement.elements(ROLE_NAME))
    {
      securityRoles.add(roleName.getTextTrim());
    }
  }

  /**
   *
   * @param realmElement Element
   * @throws SecurityConfigException
   */
  private void loadRealm(Element realmElement)
      throws SecurityConfigException
  {
    String contextPath = "";

    try
    {
      // Determine the context path from servletContext
      Class acfCls = Class.forName(
          "org.apache.catalina.core.ApplicationContextFacade");
      if (acfCls.isAssignableFrom(servletContext.getClass()))
      {
        Method getContextPath = acfCls.getMethod("getContextPath");
        contextPath = (String) getContextPath.invoke(servletContext);
      }
    }
    catch (Exception ex)
    {
      // Swallow exceptions here
    }

    String realmClass = realmElement.attributeValue(CLASSNAME_ATTRIBUTE);
    try
    {
      Class cls = Class.forName(realmClass);

      /**
       * The only reason we jump through the following hoops is so that
       * CatalinaRealm gets access to the servlet context path, otherwise we
       * would just use the default constructor for the realm.
       *
       * NOTE: we don't want to add a setServletContext() method to the Realm
       * interface at this stage because that would introduce a dependency on the
       * servlet API (though does it matter really???)
       *
       * TODO - see if this can be refactored into something more elegant
       */
      Constructor[] constructors = cls.getConstructors();
      if (constructors.length > 0 &&
          constructors[0].getParameterTypes().length == 1 &&
          constructors[0].getParameterTypes()[0].equals(String.class))
      {
        realm = (Realm) constructors[0].newInstance(contextPath);
      }
      else
        realm = (Realm) cls.newInstance();
    }
    catch (ClassNotFoundException ex)
    {
      throw new SecurityConfigException(
          String.format("Realm class [%s] not found.", realmClass), ex);
    }
    catch (Exception ex)
    {
      throw new SecurityConfigException("Error creating realm", ex);
    }
  }
}

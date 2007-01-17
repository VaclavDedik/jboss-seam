package org.jboss.seam.security.config;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
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
@Install(value = false, precedence = BUILT_IN, dependencies = "org.jboss.seam.securityManager")
@Intercept(InterceptionType.NEVER)
public class SecurityConfiguration
{
   public static final String DEFAULT_LOGIN_MODULE_NAME = "default";
   
   private static final String SECURITY_CONFIG_FILENAME = "/META-INF/security-config.xml";

   private static final LogProvider log = Logging
         .getLogProvider(SecurityConfiguration.class);

   // <security-constraint>
   private static final String SECURITY_CONSTRAINT = "security-constraint";
   private static final String WEB_RESOURCE_COLLECTION = "web-resource-collection";
   private static final String URL_PATTERN = "url-pattern";
   private static final String HTTP_METHOD = "http-method";
   private static final String AUTH_CONSTRAINT = "auth-constraint";
   private static final String ROLE_NAME = "role-name";

   // roles
   private static final String SECURITY_ROLES = "roles";
   private static final String SECURITY_ROLE = "role";
   private static final String SECURITY_MEMBERSHIPS = "memberships";
   private static final String SECURITY_PERMISSIONS = "permissions";
   private static final String SECURITY_PERMISSION = "permission";

   // login modules
   private static final String APPLICATION_POLICY = "application-policy";
   private static final String APPLICATION_POLICY_NAME = "name";
   private static final String AUTHENTICATION = "authentication";
   private static final String LOGIN_MODULE = "login-module";
   private static final String LOGIN_MODULE_CODE = "code";
   private static final String LOGIN_MODULE_FLAG = "flag";
   private static final String LOGIN_MODULE_OPTION = "module-option"; 
   private static final String LOGIN_MODULE_OPTION_NAME = "name";
   
   // login module flags
   private static final String LM_FLAG_REQUIRED = "REQUIRED";
   private static final String LM_FLAG_OPTIONAL = "OPTIONAL";
   private static final String LM_FLAG_SUFFICIENT = "SUFFICIENT";
   private static final String LM_FLAG_REQUISITE = "REQUISITE";

   private Set<SecurityConstraint> securityConstraints = new HashSet<SecurityConstraint>();
   
   public final class Role
   {
      private String name;
      
      /**
       * Memberships in other roles
       */
      private Set<String> memberships = new HashSet<String>();
      
      /**
       * Explicit permissions
       */
      private Permission[] permissions;
      
      public Role(String name)
      {
         this.name = name;
      }
      
      public String getName()
      {
         return name;
      }
      
      public Set<String> getMemberships()
      {
         return memberships;
      }
      
      public Permission[] getPermissions()
      {
         return permissions;
      }
      
      public void setPermissions(Permission[] permissions)
      {
         this.permissions = permissions;
      }
   }

   private Map<String, Role> securityRoles = new HashMap<String, Role>();

   private String securityErrorPage = "/securityError.seam";

   private LoginModuleConfiguration loginModuleConfig;

   /**
    * Initialization
    * 
    * @throws SecurityConfigException
    */
   @Create
   public void init() throws SecurityConfigException
   {
      InputStream in = Resources.getResourceAsStream(SECURITY_CONFIG_FILENAME);
      if (in != null)
         loadConfigFromStream(in);
      else
         log.warn(String.format("Security configuration file %s not found",
               SECURITY_CONFIG_FILENAME));
   }

   public static SecurityConfiguration instance()
   {
      if (!Contexts.isApplicationContextActive())
         throw new IllegalStateException("No active application context");

      SecurityConfiguration instance = (SecurityConfiguration) Component
            .getInstance(SecurityConfiguration.class, ScopeType.APPLICATION);

      if (instance == null)
      {
         throw new IllegalStateException(
               "No SecurityConfiguration could be created, make sure the Component exists in application scope");
      }

      return instance;
   }   
   
   public void setSecurityErrorPage(String securityErrorPage)
   {
      this.securityErrorPage = securityErrorPage;
   }

   public String getSecurityErrorPage()
   {
      return securityErrorPage;
   }     
   
   public Role getSecurityRole(String name)
   {
      return securityRoles.get(name);
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

         if (env.elements(SECURITY_CONSTRAINT) != null)
            loadSecurityConstraints(env.elements(SECURITY_CONSTRAINT));
         
         if (env.element(SECURITY_ROLES) != null)
            loadSecurityRoles(env.element(SECURITY_ROLES));
         
         List<Element> policies = env.elements(APPLICATION_POLICY);
         loadLoginModules(policies);
      }
      catch (Exception ex)
      {
         if (ex instanceof SecurityConfigException)
            throw (SecurityConfigException) ex;
         else
            throw new SecurityConfigException(
                  "Error loading security configuration", ex);
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
    * Returns the login module configuration
    * 
    */
   public Configuration getLoginModuleConfiguration()
   {
      return loginModuleConfig;
   }

   /**
    * Load security constraints
    * 
    * @param elements List
    * @throws SecurityConfigException
    */
   @SuppressWarnings("unchecked")
   protected void loadSecurityConstraints(List elements)
         throws SecurityConfigException
   {
      try
      {
         for (Element element : (List<Element>) elements)
         {
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraints.add(securityConstraint);

            for (Element wrcElement : (List<Element>) element
                  .elements(WEB_RESOURCE_COLLECTION))
            {
               WebResourceCollection wrc = new WebResourceCollection();
               securityConstraint.getResourceCollections().add(wrc);

               for (Element urlPatternElement : (List<Element>) wrcElement
                     .elements(URL_PATTERN))
               {
                  wrc.getUrlPatterns().add(urlPatternElement.getTextTrim());
               }

               for (Element httpMethodElement : (List<Element>) wrcElement
                     .elements(HTTP_METHOD))
               {
                  wrc.getHttpMethods().add(httpMethodElement.getTextTrim());
               }
            }

            securityConstraint.setAuthConstraint(new AuthConstraint());
            for (Element roleNameElement : (List<Element>) element.element(
                  AUTH_CONSTRAINT).elements(ROLE_NAME))
            {
               securityConstraint.getAuthConstraint().getRoles().add(
                     roleNameElement.getTextTrim());
            }
         }
      }
      catch (Exception ex)
      {
         throw new SecurityConfigException(
               "Error loading security constraints", ex);
      }
   }
   
   /**
    * Load the security roles
    * 
    * @param securityRoleElement Element
    * @throws SecurityConfigException
    */
   @SuppressWarnings("unchecked")   
   protected void loadSecurityRoles(Element securityRoleElement)
         throws SecurityConfigException
   {
      for (Element role : (List<Element>) securityRoleElement.elements(SECURITY_ROLE))
      {
         Role r = new Role(role.attributeValue("name"));

         Element m = role.element(SECURITY_MEMBERSHIPS);
         if (m != null)
         {
            for (String member : m.getTextTrim().split("[,]"))
               r.getMemberships().add(member);
         }                  

         Element permissionsElement = role.element(SECURITY_PERMISSIONS);
         if (permissionsElement != null)
         {
            List<Element> permissions = permissionsElement.elements(SECURITY_PERMISSION);
            r.setPermissions(new Permission[permissions.size()]);
            
            for (int i = 0; i < permissions.size(); i++)
            {
               r.getPermissions()[i] = new SeamPermission(
                     permissions.get(i).attributeValue("name"), 
                     permissions.get(i).attributeValue("action"));
            }
         }

         securityRoles.put(r.getName(), r);
      }
   }

   @SuppressWarnings("unchecked")
   protected void loadLoginModules(List<Element> policies)
         throws SecurityConfigException
   {
      loginModuleConfig = new LoginModuleConfiguration();
      List<AppConfigurationEntry> entries = new ArrayList<AppConfigurationEntry>();

      for (Element policy : policies)
      {      
         List<Element> modules = policy.element(AUTHENTICATION).elements(LOGIN_MODULE);
         if (modules != null)
         {
            for (Element module : modules)
            {
               Map<String, String> options = new HashMap<String, String>();
      
               for (Element option : (List<Element>) module.elements(LOGIN_MODULE_OPTION))
               {
                  options.put(option.attributeValue(LOGIN_MODULE_OPTION_NAME), 
                              option.getTextTrim());
               }
               
               AppConfigurationEntry entry = new AppConfigurationEntry(module
                     .attributeValue(LOGIN_MODULE_CODE), getControlFlag(module
                     .attributeValue(LOGIN_MODULE_FLAG)), options);
               entries.add(entry);
            }
            
            AppConfigurationEntry[] e = new AppConfigurationEntry[entries.size()];
            entries.toArray(e);
            
            if (policy.attribute(APPLICATION_POLICY_NAME) != null)
               loginModuleConfig.addEntry(policy.attributeValue(APPLICATION_POLICY_NAME), e);
            else
               loginModuleConfig.addEntry(DEFAULT_LOGIN_MODULE_NAME, e);
         }
      }
   }

   private AppConfigurationEntry.LoginModuleControlFlag getControlFlag(
         String flag) throws SecurityConfigException
   {
      if (LM_FLAG_REQUIRED.equalsIgnoreCase(flag))
         return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
      else if (LM_FLAG_OPTIONAL.equalsIgnoreCase(flag))
         return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
      else if (LM_FLAG_SUFFICIENT.equalsIgnoreCase(flag))
         return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
      else if (LM_FLAG_REQUISITE.equalsIgnoreCase(flag))
         return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
      else
         throw new SecurityConfigException(String.format(
               "Unrecognized login module control flag [%s]", flag));
   }   
}

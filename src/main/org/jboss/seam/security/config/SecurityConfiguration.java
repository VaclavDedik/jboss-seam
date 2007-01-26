package org.jboss.seam.security.config;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.security.acl.Permission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
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
@Install(value = false, precedence = BUILT_IN)
@Intercept(InterceptionType.NEVER)
public class SecurityConfiguration
{   
   private static final String SECURITY_CONFIG_FILENAME = "/META-INF/security-config.xml";

   private static final LogProvider log = Logging
         .getLogProvider(SecurityConfiguration.class);

   // roles
   private static final String SECURITY_ROLES = "roles";
   private static final String SECURITY_ROLE = "role";
   private static final String SECURITY_MEMBERSHIPS = "memberships";
   private static final String SECURITY_PERMISSIONS = "permissions";
   private static final String SECURITY_PERMISSION = "permission";
   
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

   /**
    * Initialization
    */
   @Create
   public void init()
      throws DocumentException
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
   
   public Role getSecurityRole(String name)
   {
      return securityRoles.get(name);
   }

   /**
    * Loads the security configuration from the specified InputStream.
    * 
    * @param config InputStream
    */
   protected void loadConfigFromStream(InputStream config)
      throws DocumentException
   {
      // Parse the incoming request as XML
      SAXReader xmlReader = new SAXReader();
      Document doc = xmlReader.read(config);
      Element env = doc.getRootElement();

      if (env.element(SECURITY_ROLES) != null)
         loadSecurityRoles(env.element(SECURITY_ROLES));
   }
   
   /**
    * Load the security roles
    * 
    * @param securityRoleElement Element
    */
   @SuppressWarnings("unchecked")   
   protected void loadSecurityRoles(Element securityRoleElement)
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
}

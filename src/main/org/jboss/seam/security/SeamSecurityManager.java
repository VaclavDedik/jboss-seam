package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.security.acl.Group;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.config.SecurityConfiguration;
import org.jboss.seam.security.config.SecurityConfiguration.Role;
import org.jboss.seam.security.rules.PermissionCheck;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.UnifiedELValueBinding;

/**
 * Holds configuration settings and provides functionality for the security API
 * 
 * @author Shane Bryzak
 */
@Startup(depends = "org.jboss.seam.security.securityConfiguration")
@Scope(APPLICATION)
@Name("org.jboss.seam.securityManager")
@Install(value = false, precedence = BUILT_IN)
@Intercept(InterceptionType.NEVER)
public class SeamSecurityManager
{
   private static final String SECURITY_RULES_FILENAME = "/META-INF/security-rules.drl";

   private static final String SECURITY_CONTEXT_NAME = "org.jboss.seam.security.securityContext";

   private static final LogProvider log = Logging
         .getLogProvider(SeamSecurityManager.class);

   private RuleBase securityRules;

   /**
    * Initialise the security manager
    * 
    * @throws Exception
    */
   @Create
   public void initSecurityManager() throws Exception
   {
      // Create the security rule base
      PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
      conf.setCompiler(PackageBuilderConfiguration.JANINO);

      securityRules = RuleBaseFactory.newRuleBase();
      InputStream in = Resources.getResourceAsStream(SECURITY_RULES_FILENAME);
      if (in != null)
      {
         PackageBuilder builder = new PackageBuilder(conf);
         builder.addPackageFromDrl(new InputStreamReader(in));
         securityRules.addPackage(builder.getPackage());
      }
      else
         log.warn(String.format("Security rules file %s not found",
               SECURITY_RULES_FILENAME));
   }

   /**
    * Returns the application-scoped instance of the security manager
    * 
    * @return SeamSecurityManager The application-scoped instance of the SecurityManager
    */
   public static SeamSecurityManager instance()
   {
      if (!Contexts.isApplicationContextActive())
         throw new IllegalStateException("No active application context");

      SeamSecurityManager instance = (SeamSecurityManager) Component
            .getInstance(SeamSecurityManager.class, ScopeType.APPLICATION);

      if (instance == null)
      {
         throw new IllegalStateException(
               "No SeamSecurityManager could be created, make sure the Component exists in application scope");
      }

      return instance;
   }
   
   private static Pattern EXPR_PATTERN = Pattern.compile("(hasPermission\\s*\\(\\s*'[^']*'\\s*,\\s*'[^']*')(\\s*\\))");

   /**
    * Evaluates the specified security expression, which must return a boolean
    * value.
    * 
    * @param expr String The expression to evaluate
    * @return boolean The result of the expression evaluation
    */
   public boolean evaluateExpression(String expr) 
       throws AuthorizationException
   {     
      // TODO Ugly hack!  Fix this once varargs work with EL      
      Matcher m = EXPR_PATTERN.matcher(expr);
      String replaced = m.replaceAll("$1, null$2");
      
      return (Boolean) new UnifiedELValueBinding(replaced).getValue(FacesContext.getCurrentInstance());
   }

   /**
    * Checks if the authenticated Identity is a member of the specified role.
    * 
    * @param name String The name of the role to check
    * @return boolean True if the user is a member of the specified role
    */
   public static boolean hasRole(String name)
   {
      if (!Contexts.isSessionContextActive() || !Contexts.getSessionContext().isSet(
            Seam.getComponentName(Identity.class)))
      {
         return false;
      }
     
      return Identity.instance().isUserInRole(name);
   }

   /**
    * Performs a permission check for the specified name and action
    * 
    * @param name String The permission name
    * @param action String The permission action
    * @param args Object[] Optional number of objects used to make a permission decision
    * @return boolean True if the user has the specified permission
    */
   public static boolean hasPermission(String name, String action,
         Object... args)
   {
      SeamSecurityManager mgr = instance();

      List<FactHandle> handles = new ArrayList<FactHandle>();

      PermissionCheck check = new PermissionCheck(name, action);

      WorkingMemory wm = mgr.getWorkingMemoryForSession();
      handles.add(wm.assertObject(check));

      if (args != null)
      {
         for (Object o : args)
         {
            if (o != null)
              handles.add(wm.assertObject(o));
         }
      }      

      wm.fireAllRules();

      for (FactHandle handle : handles)
         wm.retractObject(handle);

      return check.isGranted();
   }

   /**
    * Returns the security working memory for the current session
    * 
    * @return WorkingMemory
    */
   private WorkingMemory getWorkingMemoryForSession()
   {
      if (!Contexts.isSessionContextActive())
         throw new IllegalStateException("No active session context found.");
      
      if (Contexts.getSessionContext().isSet(SECURITY_CONTEXT_NAME))
         return (WorkingMemory) Contexts.getSessionContext().get(SECURITY_CONTEXT_NAME);
      else         
      {
         WorkingMemory wm = securityRules.newWorkingMemory();
  
         if (Identity.instance().isLoggedIn())
         {
            for (Principal p : Identity.instance().getSubject().getPrincipals())
            {
               wm.assertObject(p);
               
               if (p instanceof Group && "roles".equals(((Group) p).getName()))
               {
                  SecurityConfiguration config = SecurityConfiguration.instance();
                  
                  Enumeration e = ((Group) p).members();
                  while (e.hasMoreElements())
                  {
                     Principal role = (Principal) e.nextElement();
                     
                     Role r = config.getSecurityRole(role.getName());
                     if (r.getPermissions() != null)
                     {
                        for (Permission perm : r.getPermissions())
                        {
                           wm.assertObject(perm);
                        }
                     }
                  }
               }
            }
            
            // Only set the security context if the user is already logged in            
            Contexts.getSessionContext().set(SECURITY_CONTEXT_NAME, wm);
         }
         
         return wm;
      }
   }
   
   /**
    * Creates a LoginContext without a callback handler
    * 
    * @throws LoginException
    */
   public LoginContext createLoginContext()
      throws LoginException
   {
      return createLoginContext(null);
   }
      
   /**
    * A factory method for creating a LoginContext instance.  Users must use this
    * method instead of creating their own LoginContext as this factory method
    * creates a LoginContext with a custom configuration and overridden login()
    * method.
    * 
    * @param cbHandler The callback handler provided to the LoginContext
    * @throws LoginException
    */
   public LoginContext createLoginContext(CallbackHandler cbHandler)
       throws LoginException
   {     
      return new LoginContext(SecurityConfiguration.LOGIN_MODULE_NAME, 
            Identity.instance().getSubject(), cbHandler,
            SecurityConfiguration.instance().getLoginModuleConfiguration()) {
         @Override public void login() throws LoginException {
            super.login();
            populateRoles(this.getSubject());
         }
      };
   }
   
   /**
    * Populates the specified subject's roles with any inherited roles
    * according to the role memberships contained within the current 
    * SecurityConfiguration
    * 
    * @param subject The subject containing the role group.
    */
   private void populateRoles(Subject subject)
   {
      for (SimpleGroup grp : subject.getPrincipals(SimpleGroup.class))
      {
         if ("roles".equals(grp.getName()))
         {
            Set<Principal> memberships = new HashSet<Principal>();
            SecurityConfiguration config = SecurityConfiguration.instance();
            
            Enumeration e = grp.members();
            while (e.hasMoreElements())
            {
               Principal role = (Principal) e.nextElement();
               addRoleMemberships(memberships, role.getName(), config);               
            }
            
            for (Principal r : memberships)
               grp.addMember(r);
            
            break;
         }
      }
   }
   
   /**
    * Recursively adds role memberships to the specified role set, for the
    * specified role name.  The security configuration is passed in each time
    * so that a context lookup doesn't need to take place each time.
    * 
    * @param roles The set that role memberships are to be added to
    * @param roleName The name of the role to add memberships for
    * @param config The security configuration
    */
   private void addRoleMemberships(Set<Principal> roles, String roleName, 
         SecurityConfiguration config)
   {
      // Retrieve the role configuration
      Role role = config.getSecurityRole(roleName);
      
      // For each of the role's configured memberships, check if the roles
      // parameter already contains the membership.  If it doesn't add it,
      // and make a recursive call to add the membership role's memberships.
      for (String membership : role.getMemberships())
      {
         SimplePrincipal r = new SimplePrincipal(membership);
         if (!roles.contains(r))
         {
            roles.add(r);
            addRoleMemberships(roles, membership, config);
         }
      }
      
   }
   
   /**
    * Creates a callback handler that can handle a standard username/password
    * callback, using the specified username and password parameters.
    * 
    * @param username The username to provide for a NameCallback
    * @param password The password to provide for a PasswordCallback
    */
   public CallbackHandler createCallbackHandler(final String username, 
         final String password)
   {
      return new CallbackHandler() {
         public void handle(Callback[] callbacks) 
            throws IOException, UnsupportedCallbackException 
         {
            for (int i = 0; i < callbacks.length; i++)
            {
               if (callbacks[i] instanceof NameCallback)
                  ((NameCallback) callbacks[i]).setName(username);
               else if (callbacks[i] instanceof PasswordCallback)
                  ((PasswordCallback) callbacks[i]).setPassword(password.toCharArray());
               else
                  throw new UnsupportedCallbackException(callbacks[i],
                        "Unsupported callback");
            }
            
         }
      };
   }
}

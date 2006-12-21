package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.rules.PermissionCheck;
import org.jboss.seam.util.Resources;

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
    * @return SeamSecurityManager
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

   /**
    * Evaluates the specified security expression, which must return a boolean
    * value.
    * 
    * @param expr String
    * @return boolean
    */
   public boolean evaluateExpression(String expr)
   {
      return ((Boolean) Expressions.instance().createValueBinding(expr)
            .getValue());
   }

   /**
    * Checks if the authenticated Identity is a member of the specified role.
    * 
    * @param name String
    * @return boolean
    */
   public static boolean hasRole(String name)
   {
      if (!Contexts.isSessionContextActive() || !Contexts.getSessionContext().isSet(
            Seam.getComponentName(Identity.class)))
      {
         return false;
      }

      Identity ident = Identity.instance();
      if (!ident.isValid())
         return false;
      
      return ident.isUserInRole(name);
   }

   /**
    * Performs a permission check for the specified name and action
    * 
    * @param name String
    * @param action String
    * @param args Object[]
    * @return boolean
    */
   public static boolean hasPermission(String name, String action,
         Object... args)
   {
      SeamSecurityManager mgr = instance();

      List<FactHandle> handles = new ArrayList<FactHandle>();

      PermissionCheck check = new PermissionCheck(name, action);

      WorkingMemory wm = mgr.getWorkingMemoryForSession();
      handles.add(wm.assertObject(check));

      for (Object o : args)
      {
         if (o != null)
           handles.add(wm.assertObject(o));
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

      Identity ident = Identity.isSet() ? Identity.instance() : null;
      WorkingMemory wm;
      
      if (Contexts.getSessionContext().isSet(SECURITY_CONTEXT_NAME))
         wm = (WorkingMemory) Contexts.getSessionContext().get(SECURITY_CONTEXT_NAME);
      else         
      {
         if (ident != null && !ident.isValid())
            throw new IllegalStateException("Authenticated Identity is not valid");

         wm = securityRules.newWorkingMemory();
         Contexts.getSessionContext().set(SECURITY_CONTEXT_NAME, wm);
      }
      
      // Assert the identity into the working memory if one exists and it hasn't
      // been asserted before
      if (ident != null && wm.getObjects(ident.getClass()).size() > 0)
      {
         wm.assertObject(ident);

         for (Role r : ident.getRoles())
            wm.assertObject(r);

         /** @todo Assert the Identity's explicit permissions also? */      
      }      

      return wm;
   }
}

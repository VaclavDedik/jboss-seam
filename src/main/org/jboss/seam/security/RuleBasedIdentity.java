package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.base.ClassObjectFilter;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Identity implementation that supports permission
 * checking via a Drools rulebase.
 * 
 * @author Shane Bryzak
 *
 */
@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@BypassInterceptors
@Install(precedence=FRAMEWORK, classDependencies="org.drools.WorkingMemory")
@Startup
public class RuleBasedIdentity extends Identity
{  
   private static final long serialVersionUID = -2798083003251077858L;

   public static final String RULES_COMPONENT_NAME = "securityRules";   
   
   private static final LogProvider log = Logging.getLogProvider(RuleBasedIdentity.class);
   
   private StatefulSession securityContext;
   
   private RuleBase securityRules;  
   
   @Override
   public void create()
   {
      super.create();
      initSecurityContext();
   }
   
   protected void initSecurityContext()
   {
      if (securityRules==null)
      {
         securityRules = (RuleBase) Component.getInstance(RULES_COMPONENT_NAME, true);
      }
      
      if (securityRules != null)
      {
         securityContext = securityRules.newStatefulSession(false);
      }
      
      if (securityContext == null)
      {
         log.warn("no security rule base available - please install a RuleBase with the name '" +
                  RULES_COMPONENT_NAME + "' if permission checks are required.");
      }
   }

   @Override
   protected void postAuthenticate()
   {
      super.postAuthenticate();

      if (getSecurityContext() != null)
      {         
         getSecurityContext().insert(getPrincipal());
      }
   }
   
   /**
    * Performs a permission check for the specified name and action
    * 
    * @param name String The permission name
    * @param action String The permission action
    * @param arg Object Optional object parameter used to make a permission decision
    * @return boolean True if the user has the specified permission
    */
   @Override
   public boolean hasPermission(String name, String action, Object...arg)
   {      
      if (!securityEnabled) return true;
      
      StatefulSession securityContext = getSecurityContext();
      
      if (securityContext == null) return false;      
      
      synchronizeContext();
      
      List<FactHandle> handles = new ArrayList<FactHandle>();

      PermissionCheck check = new PermissionCheck(name, action);
      
      synchronized( securityContext )
      {
         handles.add( securityContext.insert(check) );
         
         for (int i = 0; i < arg.length; i++)
         {
            if (i == 0 && arg[0] instanceof Collection)
            {
               for (Object value : (Collection) arg[i])
               {
                  if ( securityContext.getFactHandle(value) == null )
                  {
                     handles.add( securityContext.insert(value) );
                  }
               }               
            }
            else
            {
               handles.add( securityContext.insert(arg[i]) );
            }
         }
   
         securityContext.fireAllRules();
   
         for (FactHandle handle : handles)
            securityContext.retract(handle);
      }
      
      return check.isGranted();
   }
   
   @SuppressWarnings("unchecked")
   @Override   
   public void unAuthenticate()
   {
      super.unAuthenticate();
      setSecurityContext(null);
      initSecurityContext();
   }
   
   /**
    *  Synchronizes the state of the security context with that of the subject
    */
   private void synchronizeContext()
   {
      if (getSecurityContext() != null)
      {
         for ( Group sg : getSubject().getPrincipals(Group.class) )      
         {
            if ( ROLES_GROUP.equals( sg.getName() ) )
            {
               Enumeration e = sg.members();
               while (e.hasMoreElements())
               {
                  Principal role = (Principal) e.nextElement();
   
                  boolean found = false;
                  Iterator<Role> iter = getSecurityContext().iterateObjects(new ClassObjectFilter(Role.class)); 
                  while (iter.hasNext()) 
                  {
                     Role r = iter.next();
                     if (r.getName().equals(role.getName()))
                     {
                        found = true;
                        break;
                     }
                  }
                  
                  if (!found)
                  {
                     getSecurityContext().insert(new Role(role.getName()));
                  }
                  
               }
            }
         }    
         
         Iterator<Role> iter = getSecurityContext().iterateObjects(new ClassObjectFilter(Role.class)); 
         while (iter.hasNext()) 
         {
            Role r = iter.next();
            if (!super.hasRole(r.getName()))
            {
               FactHandle fh = getSecurityContext().getFactHandle(r);
               getSecurityContext().retract(fh);
            }
         }
      }
   }
   
   
   public StatefulSession getSecurityContext()
   {
      return securityContext;
   }
   
   public void setSecurityContext(StatefulSession securityContext)
   {
      this.securityContext = securityContext;
   }
   

   public RuleBase getSecurityRules()
   {
      return securityRules;
   }

   public void setSecurityRules(RuleBase securityRules)
   {
      this.securityRules = securityRules;
   }   
   
   @Override
   public void logout()
   {
      // Explicitly destroy the security context
      if (securityContext != null)
      {
         securityContext.dispose();
         securityContext = null;
      }
      
      super.logout();
   }   
}

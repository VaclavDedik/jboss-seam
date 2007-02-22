package org.jboss.seam.security;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Intercept(NEVER)
@Install(precedence=FRAMEWORK, classDependencies="org.drools.WorkingMemory")
@Startup
public class RuleBasedIdentity extends Identity
{  
   public static final String RULES_COMPONENT_NAME = "securityRules";   
   
   private static final LogProvider log = Logging.getLogProvider(RuleBasedIdentity.class);
   
   private WorkingMemory securityContext;
   
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
         securityContext = securityRules.newWorkingMemory(false);
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
      
      WorkingMemory securityContext = getSecurityContext();

      if (securityContext != null)
      {
         // Populate the working memory with the user's principals
         for ( Principal p : getSubject().getPrincipals() )
         {         
            if ( (p instanceof Group) && "roles".equals( ( (Group) p ).getName() ) )
            {
               Enumeration e = ( (Group) p ).members();
               while ( e.hasMoreElements() )
               {
                  Principal role = (Principal) e.nextElement();
                  securityContext.assertObject( new Role( role.getName() ) );
               }
            }     
         }
         
         securityContext.assertObject(getPrincipal());
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
      WorkingMemory securityContext = getSecurityContext();
      
      if (securityContext == null) return false;      
      
      List<FactHandle> handles = new ArrayList<FactHandle>();

      PermissionCheck check = new PermissionCheck(name, action);
      
      synchronized( securityContext )
      {
         handles.add( securityContext.assertObject(check) );
         
         for (int i = 0; i < arg.length; i++)
         {
            if (i == 0 && arg[0] instanceof Collection)
            {
               for (Object value : (Collection) arg[i])
               {
                  if ( securityContext.getFactHandle(value) == null )
                  {
                     handles.add( securityContext.assertObject(value) );
                  }
               }               
            }
            else
            {
               handles.add( securityContext.assertObject(arg[i]) );
            }
         }
   
         securityContext.fireAllRules();
   
         for (FactHandle handle : handles)
            securityContext.retractObject(handle);
      }
      
      return check.isGranted();
   }
   
   @Override
   protected void unAuthenticate()
   {
      WorkingMemory securityContext = getSecurityContext();
      
      if (securityContext != null)
      {
         for (Role role : (List<Role>) securityContext.getObjects(Role.class))
         {
            getSecurityContext().retractObject(securityContext.getFactHandle(role));
         }
      }
      
      super.unAuthenticate();
   }
   
   @Override
   public boolean addRole(String role)
   {
      if (super.addRole(role)) 
      {
         WorkingMemory securityContext = getSecurityContext();
         
         if (securityContext != null)
         {
            getSecurityContext().assertObject(new Role(role));
            return true;
         }
      }

      return false;
   }
   
   @Override
   public void removeRole(String role)
   {
      WorkingMemory securityContext = getSecurityContext();      
      
      if (securityContext != null)
      {
         for (Role r : (List<Role>) getSecurityContext().getObjects(Role.class))
         {
            if (r.getName().equals(role))
            {
               FactHandle fh = getSecurityContext().getFactHandle(r);
               getSecurityContext().retractObject(fh);
               break;
            }
         }
      }
         
      super.removeRole(role);
   }
   
   
   public WorkingMemory getSecurityContext()
   {
      return securityContext;
   }
   
   public void setSecurityContext(WorkingMemory securityContext)
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
}

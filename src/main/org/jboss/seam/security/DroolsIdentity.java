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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Intercept(NEVER)
@Install(precedence=FRAMEWORK, classDependencies="org.drools.WorkingMemory")
@Startup
public class DroolsIdentity extends Identity
{  
   public static final String RULES_COMPONENT_NAME = "securityRules";   
   
   private WorkingMemory securityContext;
   
   @In
   private RuleBase securityRules;
   
   @Override
   public void create()
   {
      super.create();
      initSecurityContext();
   }
   
   protected void initSecurityContext()
   {
      if (securityRules==null) //it might have been configured via components.xml
      {
         securityRules = (RuleBase) Component.getInstance(RULES_COMPONENT_NAME, true);
      }
      
      assertSecurityContextExists();      
      securityContext = securityRules.newWorkingMemory(false);
   }

   
   @Override
   protected void postAuthenticate()
   {
      super.postAuthenticate();
      
      WorkingMemory securityContext = getSecurityContext();
      assertSecurityContextExists();

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

   private void assertSecurityContextExists()
   {
      if (securityContext==null)
      {
         throw new IllegalStateException(
            "no security rule base available - please install a RuleBase with the name '" +
            RULES_COMPONENT_NAME + "'");
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
      List<FactHandle> handles = new ArrayList<FactHandle>();

      PermissionCheck check = new PermissionCheck(name, action);

      WorkingMemory securityContext = getSecurityContext();
      assertSecurityContextExists();
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
      for (Role role : (List<Role>) getSecurityContext().getObjects(Role.class))
      {
         getSecurityContext().retractObject(getSecurityContext().getFactHandle(role));
      }
      
      super.unAuthenticate();
   }
   
   @Override
   public boolean addRole(String role)
   {
      if (super.addRole(role)) 
      {
         getSecurityContext().assertObject(new Role(role));
         return true;
      }
      else
      {
         return false;
      }
   }
   
   @Override
   public void removeRole(String role)
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

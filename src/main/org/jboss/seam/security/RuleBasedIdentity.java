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
      
      StatefulSession securityContext = getSecurityContext();

      if (securityContext != null)
      {
         // Populate the working memory with the user's principals
         for ( Principal p : getSubject().getPrincipals() )
         {         
            if ( (p instanceof Group) && ROLES_GROUP.equals( ( (Group) p ).getName() ) )
            {
               Enumeration e = ( (Group) p ).members();
               while ( e.hasMoreElements() )
               {
                  Principal role = (Principal) e.nextElement();
                  securityContext.insert( new Role( role.getName() ) );
               }
            }     
         }
         
         securityContext.insert(getPrincipal());
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
      StatefulSession securityContext = getSecurityContext();
      
      if (securityContext == null) return false;      
      
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
   
   /**
    * Overridden version of hasRole() that checks for the existence of the role
    * in the security context first.  If it is not found there, then the super
    * method is invoked instead.
    */
   @Override
   public boolean hasRole(String role)
   {
      Iterator<Role> iter = securityContext.iterateObjects(new ClassObjectFilter(Role.class));
      
      while (iter.hasNext())
      {
         Role r = iter.next();
         if (r.getName().equals(role)) return true;
      }
      
      return super.hasRole(role);
   }
   
   @SuppressWarnings("unchecked")
   @Override   
   protected void unAuthenticate()
   {
      StatefulSession securityContext = getSecurityContext();
      
      if (securityContext != null)
      {
         Iterator<Role> iter = securityContext.iterateObjects(new ClassObjectFilter(Role.class)); 
         while (iter.hasNext()) 
         {
            getSecurityContext().retract(securityContext.getFactHandle(iter.next()));
         }
      }
      
      super.unAuthenticate();
   }
   
   @Override
   public boolean addRole(String role)
   {
      if (super.addRole(role)) 
      {
         StatefulSession securityContext = getSecurityContext();
         
         if (securityContext != null)
         {
            getSecurityContext().insert(new Role(role));
            return true;
         }
      }

      return false;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public void removeRole(String role)
   {
      StatefulSession securityContext = getSecurityContext();      
      
      if (securityContext != null)
      {
         Iterator<Role> iter = securityContext.iterateObjects(new ClassObjectFilter(Role.class)); 
         while (iter.hasNext()) 
         {
            Role r = iter.next();
            if (r.getName().equals(role))
            {
               FactHandle fh = getSecurityContext().getFactHandle(r);
               getSecurityContext().retract(fh);
               break;
            }
         }
      }
         
      super.removeRole(role);
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
}

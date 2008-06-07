package org.jboss.seam.security;

import java.security.Principal;
import java.security.acl.Group;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

/**
 * Defines a security operation that can be executed within a particular 
 * security context.
 * 
 * @author Shane Bryzak
 */
public abstract class RunAsOperation
{
   private Principal principal;
   private Subject subject;
   
   private Set<String> roles;
   
   private boolean systemOp = false;
   
   public RunAsOperation()
   {
      principal = new SimplePrincipal(null);  
      subject = new Subject();
      roles = new HashSet<String>();
   }
   
   /**
    * A system operation allows any security checks to pass
    * 
    * @param systemOp
    */
   public RunAsOperation(boolean systemOp)
   {      
      this();
      systemOp = true;
   }
   
   public abstract void execute();
   
   public Principal getPrincipal()
   {
      return principal;
   }
   
   public Subject getSubject()
   {
      return subject;
   }
   
   public RunAsOperation addRole(String role)
   {
      roles.add(role);      
      return this;
   }
   
   public boolean isSystemOperation()
   {
      return systemOp;
   }
   
   public void run()
   {      
      for (String role : roles)
      {
         for ( Group sg : getSubject().getPrincipals(Group.class) )      
         {
            if ( Identity.ROLES_GROUP.equals( sg.getName() ) )
            {
               sg.addMember(new SimplePrincipal(role));
               break;
            }
         }
                  
         SimpleGroup roleGroup = new SimpleGroup(Identity.ROLES_GROUP);
         roleGroup.addMember(new SimplePrincipal(role));
         getSubject().getPrincipals().add(roleGroup);
      }      
      
      Identity.instance().runAs(this);
   }
}

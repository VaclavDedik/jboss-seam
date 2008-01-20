package org.jboss.seam.security;

import java.security.Principal;
import java.security.acl.Group;

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
   
   public RunAsOperation()
   {
      principal = new SimplePrincipal(null);  
      subject = new Subject();
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
   
   public String[] getRoles()
   {
      return null;
   }
   
   private boolean addRole(String role)
   {
      for ( Group sg : getSubject().getPrincipals(Group.class) )      
      {
         if ( Identity.ROLES_GROUP.equals( sg.getName() ) )
         {
            return sg.addMember(new SimplePrincipal(role));
         }
      }
               
      SimpleGroup roleGroup = new SimpleGroup(Identity.ROLES_GROUP);
      roleGroup.addMember(new SimplePrincipal(role));
      getSubject().getPrincipals().add(roleGroup);
      
      return true;
   }
   
   public void run()
   {
      String[] roles = getRoles();
      if (roles != null)
      {
         for (String role : getRoles())
         {
            addRole(role);
         }
      }
      
      Identity.instance().runAs(this);
   }
}

package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Set;

import javax.security.auth.Subject;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Install(precedence = BUILT_IN, dependencies = "org.jboss.seam.securityManager")
public class Identity implements Serializable
{  
   private static final long serialVersionUID = 3751659008033189259L;

   protected Principal principal;
   
   protected Subject subject;
   
   public Identity()
   {
      subject = new Subject();
   }

   public static Identity instance()
   {
      if (!Contexts.isSessionContextActive())
         throw new IllegalStateException("No active session context");

      Identity instance = (Identity) Component.getInstance(Identity.class,
            ScopeType.SESSION, true);

      if (instance == null)
      {
         throw new IllegalStateException(
               "No Identity could be created");
      }

      return instance;
   }
   
   /**
    * If there is a principal set, then the user is logged in.
    * 
    */
   public boolean isLoggedIn()
   {
      return getPrincipal() != null;
   }

   public Principal getPrincipal()
   {
      if (principal == null)
      {
         Set<Principal> principals = subject.getPrincipals(Principal.class);
         if (!principals.isEmpty())
            principal = principals.iterator().next();
      }
      
      return principal;
   }
   
   public Subject getSubject()
   {
      return subject;
   }

   /**
    * Checks if the authenticated user contains the specified role.
    * 
    * @param role String
    * @return boolean Returns true if the authenticated user contains the role,
    *         or false if otherwise.
    */
   public boolean isUserInRole(String role)
   {
      for (Group sg : subject.getPrincipals(Group.class))      
      {
         if ("roles".equals(sg.getName()))
         {
            return sg.isMember(new SimplePrincipal(role));
         }
      }
      
      return false;
   }
      
   /**
    * Performs an authorization check, based on the specified security expression.
    * 
    * @param expr The security expression to evaluate
    * @throws NotLoggedInException Thrown if the user is not authenticated
    * @throws AuthorizationException if the authorization check fails
    */
   public void checkRestriction(String expr)
   {
      if (!isLoggedIn())
         throw new NotLoggedInException();
      
      if (!Security.instance().evaluateExpression(expr))
         throw new AuthorizationException(String.format(
               "Authorization check failed for expression [%s]", expr));      
   }
}

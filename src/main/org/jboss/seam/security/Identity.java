package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.security.Principal;

import javax.security.auth.Subject;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Install(precedence = BUILT_IN, dependencies = "org.jboss.seam.securityManager")
public class Identity implements Serializable
{
   protected boolean authenticated;

   protected boolean valid;
   
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
               "No Identity exists in session scope");
      }

      return instance;
   }

   public static boolean isSet()
   {
      return Contexts.isSessionContextActive()
            && Contexts.getSessionContext().isSet(
                  Seam.getComponentName(Identity.class));
   }

   public Principal getPrincipal()
   {
      return principal;
   }
   
   public Subject getSubject()
   {
      return subject;
   }

   public final boolean isAuthenticated()
   {
      return authenticated;
   }

   public final boolean isValid()
   {
      return valid;
   }

   public final void invalidate()
   {
      valid = false;
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
//      for (Role r : getRoles())
//      {
//         if (r.getName().equals(role))
//            return true;
//      }
      return false;
   }
}

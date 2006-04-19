package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.security.Principal;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;

/**
 * Manager component for the current user Principal
 * exposed via the JSF ExternalContext.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("userPrincipal")
public class UserPrincipal
{
   @Unwrap
   public Principal getUserPrincipal()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      return facesContext==null ? null : facesContext.getExternalContext().getUserPrincipal();
   }
   
   public Principal instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (Principal) Component.getInstance(UserPrincipal.class, ScopeType.APPLICATION, false);
   }
   
}

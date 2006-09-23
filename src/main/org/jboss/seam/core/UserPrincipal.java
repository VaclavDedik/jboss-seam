package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.security.Principal;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

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
      if ( facesContext != null ) 
      {
         return facesContext.getExternalContext().getUserPrincipal();
      }
      
      ServletRequest servletRequest = Lifecycle.getServletRequest();
      if ( servletRequest != null )
      {
         return ( (HttpServletRequest) servletRequest ).getUserPrincipal();
      }
      
      return null;
   }
   
   public static Principal instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (Principal) Component.getInstance(UserPrincipal.class, ScopeType.APPLICATION, false);
   }
   
}

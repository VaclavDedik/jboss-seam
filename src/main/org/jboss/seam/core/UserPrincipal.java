package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.security.Principal;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;

@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("userPrincipal")
public class UserPrincipal
{
   @Unwrap
   public Principal getMap()
   {
      return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
   }
}

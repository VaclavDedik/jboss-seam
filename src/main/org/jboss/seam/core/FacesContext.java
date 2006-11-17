//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Support for injecting the JSF FacesContext object
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.facesContext")
public class FacesContext
{
   @Unwrap
   public javax.faces.context.FacesContext getContext()
   {
      return javax.faces.context.FacesContext.getCurrentInstance();
   }
}

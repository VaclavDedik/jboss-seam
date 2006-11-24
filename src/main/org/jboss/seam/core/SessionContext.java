//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

/**
 * Support for injecting the session context
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.sessionContext")
@Install(precedence=BUILT_IN)
public class SessionContext
{
   @Unwrap
   public Context getContext()
   {
      return Contexts.isSessionContextActive() ? 
            Contexts.getSessionContext() : null;
   }
}

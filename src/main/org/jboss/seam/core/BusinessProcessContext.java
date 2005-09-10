//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

/**
 * Support for injecting the business process context
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("businessProcessContext")
public class BusinessProcessContext
{
   @Unwrap
   public Context getContext()
   {
      return Contexts.isBusinessProcessContextActive() ? 
            Contexts.getBusinessProcessContext(): null;
   }
}

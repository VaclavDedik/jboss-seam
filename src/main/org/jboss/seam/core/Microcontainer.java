//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.kernel.plugins.bootstrap.standalone.StandaloneBootstrap;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

/**
 * A seam component that bootstraps the JBoss microcontainer
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("org.jboss.seam.core.microcontainer")
public class Microcontainer
{
   @Create 
   public void startup() throws Exception
   {
      StandaloneBootstrap.main(null);
   }
}

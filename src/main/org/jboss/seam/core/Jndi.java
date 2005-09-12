//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.naming.InitialContext;

import org.apache.commons.logging.LogFactory;
import org.jboss.logging.Logger;
import org.jboss.naming.Util;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jnp.server.SingletonNamingServer;

/**
 * A seam component that bootstraps the embedded EJB container
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("org.jboss.seam.core.jndi")
public class Jndi
{
   
   private static final Logger log = Logger.getLogger(Jndi.class);
   
   private SingletonNamingServer namingServer;

   @Create
   public void startup() throws Exception
   {
      //start up JNDI
      log.info("starting JNDI server");
      namingServer = new SingletonNamingServer();
      InitialContext ctx = new InitialContext();
      Util.createSubcontext(ctx, "java:/comp");
   }

   @Destroy
   public void shutdown()
   {
      namingServer = null;
   }

}

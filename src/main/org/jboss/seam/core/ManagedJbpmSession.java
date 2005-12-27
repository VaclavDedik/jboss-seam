/*
?* JBoss, Home of Professional Open Source
?*
?* Distributable under LGPL license.
?* See terms of license at gnu.org.
?*/
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.NamingHelper;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;

/**
 * Manages a reference to a JbpmSession.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope( ScopeType.EVENT )
@Name( "jbpmSession" )
@Intercept( NEVER )
public class ManagedJbpmSession
{
   private static final Logger log = Logger.getLogger(ManagedJbpmSession.class);

   private JbpmSession jbpmSession;

   @Create
   public void create(Component component) throws NamingException
   {
      jbpmSession = getSessionFactory().openJbpmSession();
      log.debug( "created seam managed jBPM session" );
   }

   @Unwrap
   public JbpmSession getJbpmSession()
   {
      return jbpmSession;
   }

   @Destroy
   public void destroy()
   {
      log.debug( "destroying seam managed jBPM session" );
      jbpmSession.close();
   }

   private JbpmSessionFactory getSessionFactory() throws NamingException
   {
      InitialContext ctx = NamingHelper.getInitialContext();
      return (JbpmSessionFactory) ctx.lookup( Init.instance().getJbpmSessionFactoryName() );
   }
   
   public static JbpmSession instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("no active event context");
      }
      return (JbpmSession) Component.getInstance(ManagedJbpmSession.class, ScopeType.EVENT, true);
   }

}

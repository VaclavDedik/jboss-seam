/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.logging.Logger;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;

import javax.naming.NamingException;
import javax.naming.InitialContext;

/**
 * Implementation of ManagedJbpmSession.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope(ScopeType.CONVERSATION)
public class ManagedJbpmSession {

   private static final Logger log = Logger.getLogger( ManagedJbpmSession.class );

	private String jbpmSessionFactoryName;
	private JbpmSession jbpmSession;

	@Create
	public void create(Component component) {
		jbpmSessionFactoryName = component.getName();
		jbpmSession = getSessionFactory().openJbpmSession();

		log.info( "created seam managed jbpm-session ["+ jbpmSessionFactoryName + "]" );
	}

	@Unwrap
	public JbpmSession getJbpmSession() {
		return jbpmSession;
	}

	@Destroy
	public void destroy() {
		log.info( "destroying seam managed jbpm-session [" + jbpmSessionFactoryName + "]" );
		jbpmSession.close();
	}

   private JbpmSessionFactory getSessionFactory() {
	   InitialContext ctx = null;
	   try {
		   ctx = new InitialContext();
		   return ( JbpmSessionFactory ) ctx.lookup( jbpmSessionFactoryName );
	   }
	   catch( NamingException e ) {
		   throw new IllegalArgumentException( "JbpmSessionFactory [" + jbpmSessionFactoryName + "] not found", e );
	   }
	   finally {
		   if ( ctx != null ) {
			   try {
				   ctx.close();
			   }
			   catch( Throwable ignore ) {
			   }
		   }
	   }
   }

   public String toString()
   {
      return "ManagedJbpmSession(" + jbpmSessionFactoryName + ")";
   }
}

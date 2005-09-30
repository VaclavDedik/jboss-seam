/*
?* JBoss, Home of Professional Open Source
?*
?* Distributable under LGPL license.
?* See terms of license at gnu.org.
?*/
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.Hashtable;

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
   private static final Logger log = Logger.getLogger( ManagedJbpmSession.class );

   private String jbpmSessionFactoryName;
   private JbpmSession jbpmSession;
   private Hashtable initialContextProperties;

   @Create
   public void create(Component component)
   {
      Init settings = Init.instance();
      jbpmSessionFactoryName = settings.getJbpmSessionFactoryName();

      log.debug( "created seam managed jbpm-session [" + jbpmSessionFactoryName + "]" );
   }

   @Unwrap
   public JbpmSession getJbpmSession()
   {
      if ( jbpmSession == null )
      {
         jbpmSession = getSessionFactory().openJbpmSession();
      }
      return jbpmSession;
   }

   @Destroy
   public void destroy()
   {
      log.debug( "destroying seam managed jbpm-session [" + jbpmSessionFactoryName + "]" );
      if ( jbpmSession != null )
      {
         jbpmSession.close();
      }
   }

   private JbpmSessionFactory getSessionFactory()
   {
      InitialContext ctx = null;
      try
      {
         ctx = (initialContextProperties != null) ? new InitialContext(initialContextProperties) : new InitialContext();
         return ( JbpmSessionFactory ) ctx.lookup( jbpmSessionFactoryName );
      }
      catch ( NamingException e )
      {
         throw new IllegalArgumentException( "JbpmSessionFactory [" + jbpmSessionFactoryName + "] not found", e );
      }
      finally
      {
         release( ctx );
      }
   }

   private void release(InitialContext ctx)
   {
      if ( ctx != null )
      {
         try
         {
            ctx.close();
         }
         catch ( Throwable ignore )
         {
            // ignore
         }
      }
   }

   public String toString()
   {
      return "ManagedJbpmSession(" + jbpmSessionFactoryName + ")";
   }

   public void setInitialContextProperties(Hashtable initialContextProperties)
   {
      this.initialContextProperties = initialContextProperties;
   }
}

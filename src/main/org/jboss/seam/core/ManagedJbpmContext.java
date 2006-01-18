/*
?* JBoss, Home of Professional Open Source
?*
?* Distributable under LGPL license.
?* See terms of license at gnu.org.
?*/
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

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
import org.jboss.seam.contexts.Lifecycle;
import org.jbpm.JbpmContext;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;

/**
 * Manages a reference to a JbpmSession.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope( ScopeType.EVENT )
@Name( "jbpmContext" )
@Intercept( NEVER )
public class ManagedJbpmContext
{
   private static final Logger log = Logger.getLogger(ManagedJbpmContext.class);

   private JbpmContext jbpmContext;
   //private int counter;

   @Create
   public void create() throws NamingException
   {
      jbpmContext = Jbpm.instance().getJbpmConfiguration().createJbpmContext();
      //counter++;
      log.debug( "created seam managed jBPM context");
   }

   @Unwrap
   public JbpmContext getJbpmContext()
   {
      return jbpmContext;
   }

   @Destroy
   public void destroy()
   {
      if ( Lifecycle.isException() )
      {
         DbPersistenceServiceFactory dpsf = (DbPersistenceServiceFactory) jbpmContext.getJbpmConfiguration()
               .getServiceFactory(Services.SERVICENAME_PERSISTENCE);
         if ( dpsf.isTransactionEnabled() )
         {
            jbpmContext.setRollbackOnly();
         }
      }
      else 
      {
         org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
         if (processInstance!=null) 
         {
            jbpmContext.save( processInstance );
         }
      }
      log.debug( "destroying seam managed jBPM context" );
      //counter--;
      jbpmContext.close();
   }
   
   public static JbpmContext instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("no active event context");
      }
      return (JbpmContext) Component.getInstance(ManagedJbpmContext.class, ScopeType.EVENT, true);
   }

}

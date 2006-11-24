/*
?* JBoss, Home of Professional Open Source
?*
?* Distributable under LGPL license.
?* See terms of license at gnu.org.
?*/
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.naming.NamingException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.util.Transactions;
import org.jbpm.JbpmContext;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;

/**
 * Manages a reference to a JbpmSession.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.jbpmContext")
@Intercept(NEVER)
@Install(precedence=BUILT_IN, dependencies="org.jboss.seam.core.jbpm")
public class ManagedJbpmContext implements Synchronization
{
   private static final Log log = LogFactory.getLog(ManagedJbpmContext.class);

   private JbpmContext jbpmContext;
   private boolean synchronizationRegistered;

   @Create
   public void create() throws NamingException, RollbackException, SystemException
   {
      jbpmContext = Jbpm.instance().getJbpmConfiguration().createJbpmContext();
      assertNoTransactionManagement();
      log.debug( "created seam managed jBPM context");
   }

   private void assertNoTransactionManagement()
   {
      DbPersistenceServiceFactory dpsf = (DbPersistenceServiceFactory) jbpmContext.getJbpmConfiguration()
            .getServiceFactory(Services.SERVICENAME_PERSISTENCE);
      if ( dpsf.isTransactionEnabled() )
      {
         throw new IllegalStateException("jBPM transaction management is enabled, disable in jbpm.cfg.xml");
      }
   }

   @Unwrap
   public JbpmContext getJbpmContext() throws NamingException, RollbackException, SystemException
   {
      if ( !Transactions.isTransactionActiveOrMarkedRollback() )
      {
         throw new IllegalStateException("JbpmContext may only be used inside a transaction");
      }
      if ( !synchronizationRegistered && !Lifecycle.isDestroying() && Transactions.isTransactionActive() )
      {
         jbpmContext.getSession().getTransaction().registerSynchronization(this);
         //Transactions.registerSynchronization(this);
         synchronizationRegistered = true;
      }
      return jbpmContext;
   }
   
   public void beforeCompletion()
   {
      log.debug( "flushing seam managed jBPM context" );
      org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
      if (processInstance!=null) 
      {
         jbpmContext.save(processInstance);
      }
      log.debug( "flushing business process context" );
      Contexts.getBusinessProcessContext().flush();
      jbpmContext.getSession().flush();
   }
   
   public void afterCompletion(int status) {
      synchronizationRegistered = false;
   }

   @Destroy
   public void destroy()
   {
      log.debug( "destroying seam managed jBPM context" );
      //jbpmContext.setRollbackOnly(); //TODO: this currently has no effect (bug in jBPM)
      jbpmContext.getSession().clear(); //WORKAROUND for jBPM bug, prevent flushing of session!
      jbpmContext.close();
   }
      
   public static JbpmContext instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("no active event context");
      }
      return (JbpmContext) Component.getInstance(ManagedJbpmContext.class, ScopeType.EVENT);
   }

}

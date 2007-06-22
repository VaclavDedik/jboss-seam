//$Id$
package org.jboss.seam.persistence;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.transaction.Synchronization;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.AbstractTransactionListener;
import org.jboss.seam.core.Mutable;
import org.jboss.seam.core.TransactionListener;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.Naming;

/**
 * A Seam component that manages a conversation-scoped extended
 * persistence context that can be shared by arbitrary other
 * components.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ManagedHibernateSession 
   implements Serializable, HttpSessionActivationListener, Mutable, PersistenceContextManager, Synchronization
{
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 3130309555079841107L;

   private static final LogProvider log = Logging.getLogProvider(ManagedHibernateSession.class);
   
   private Session session;
   private String sessionFactoryJndiName;
   private String componentName;
   private ValueExpression<SessionFactory> sessionFactory;
   private List<Filter> filters = new ArrayList<Filter>(0);
   
   private transient boolean synchronizationRegistered;
   
   private static Constructor FULL_TEXT_SESSION_PROXY_CONSTRUCTOR;
   private static Method FULL_TEXT_SESSION_CONSTRUCTOR;
   static 
   {
      try
      {
         Class searchClass = Class.forName("org.hibernate.search.Search");
         FULL_TEXT_SESSION_CONSTRUCTOR = searchClass.getDeclaredMethod("createFullTextSession", Session.class);
         Class fullTextSessionProxyClass = Class.forName("org.jboss.seam.persistence.FullTextHibernateSessionProxy");
         Class fullTextSessionClass = Class.forName("org.hibernate.search.FullTextSession");
         FULL_TEXT_SESSION_PROXY_CONSTRUCTOR = fullTextSessionProxyClass.getDeclaredConstructor(fullTextSessionClass);
         log.info("Hibernate Search is available :-)");
      }
      catch (Exception e)
      {
         log.info("no Hibernate Search, sorry :-(", e);
      }
   }
   
   public boolean clearDirty()
   {
      return true;
   }

   @Create
   public void create(Component component)
   {
      this.componentName = component.getName();
      if (sessionFactoryJndiName==null)
      {
         sessionFactoryJndiName = "java:/" + componentName;
      }
            
      PersistenceContexts.instance().touch(componentName);
   }

   private void initSession() throws Exception
   {
      session = getSessionFactoryFromJndiOrValueBinding().openSession();
      if (FULL_TEXT_SESSION_PROXY_CONSTRUCTOR==null)
      {
         session = new HibernateSessionProxy(session);
      }
      else
      {
         session = (Session) FULL_TEXT_SESSION_PROXY_CONSTRUCTOR.newInstance( FULL_TEXT_SESSION_CONSTRUCTOR.invoke(null, session) );
      }
      setSessionFlushMode( PersistenceContexts.instance().getFlushMode() );
      for (Filter f: filters)
      {
         if ( f.isFilterEnabled() )
         {
            enableFilter(f);
         }
      }

      if ( log.isDebugEnabled() )
      {
         log.debug("created seam managed session for session factory: "+ sessionFactoryJndiName);
      }
   }

   private void enableFilter(Filter f)
   {
      org.hibernate.Filter filter = session.enableFilter( f.getName() );
      for ( Map.Entry<String, ValueExpression> me: f.getParameters().entrySet() )
      {
         filter.setParameter( me.getKey(), me.getValue().getValue() );
      }
      filter.validate();
   }
   
   @Unwrap
   public Session getSession() throws Exception
   {
      if (session==null) initSession();
      
      //join the transaction
      if ( !synchronizationRegistered && !Lifecycle.isDestroying() && Transaction.instance().isActive() )
      {
         session.isOpen();
         TransactionListener transactionListener = AbstractTransactionListener.instance();
         if (transactionListener!=null)
         {
            transactionListener.registerSynchronization(this);
         }
         else
         {
            session.getTransaction().registerSynchronization(this);
         }
         synchronizationRegistered = true;
      }
      
      return session;
   }
   
   //we can't use @PrePassivate because it is intercept NEVER
   public void sessionWillPassivate(HttpSessionEvent event)
   {
      if ( session!=null && !session.isDirty() )
      {
         session.close();
         session = null;
      }
   }
   
   //we can't use @PostActivate because it is intercept NEVER
   public void sessionDidActivate(HttpSessionEvent event) {}
   
   @Destroy
   public void destroy()
   {
      if ( !synchronizationRegistered )
      {
         //in requests that come through SeamPhaseListener,
         //there can be multiple transactions per request,
         //but they are all completed by the time contexts
         //are destroyed
         //so wait until the end of the request to close
         //the session
         //on the other hand, if we are still waiting for
         //the transaction to commit, leave it open
         close();
      }
      PersistenceContexts.instance().untouch(componentName);
   }

   public void afterCompletion(int status)
   {
      synchronizationRegistered = false;
      if ( !Contexts.isConversationContextActive() )
      {
         //in calls to MDBs and remote calls to SBs, the 
         //transaction doesn't commit until after contexts
         //are destroyed, so wait until the transaction
         //completes before closing the session
         //on the other hand, if we still have an active
         //conversation context, leave it open
         close();
      }
   }
   
   public void beforeCompletion() {}
   
   private void close()
   {
      if ( log.isDebugEnabled() )
      {
         log.debug("destroying seam managed session for session factory: " + sessionFactoryJndiName);
      }
      if (session!=null)
      {
         session.close();
      }
   }
   
   private SessionFactory getSessionFactoryFromJndiOrValueBinding()
   {
      SessionFactory result = null;
      //first try to find it via the value binding
      if (sessionFactory!=null)
      {
         result = sessionFactory.getValue();
      }
      //if its not there, try JNDI
      if (result==null)
      {
         try
         {
            result = (SessionFactory) Naming.getInitialContext().lookup(sessionFactoryJndiName);
         }
         catch (NamingException ne)
         {
            throw new IllegalArgumentException("SessionFactory not found in JNDI: " + sessionFactoryJndiName, ne);
         }
      }
      return result;
   }
   
   public String getComponentName() {
      return componentName;
   }
   
   public void changeFlushMode(FlushModeType flushMode)
   {
      if (session!=null)
      {
         setSessionFlushMode(flushMode);
      }
   }

   protected void setSessionFlushMode(FlushModeType flushMode)
   {
      switch (flushMode)
      {
         case AUTO:
            session.setFlushMode(FlushMode.AUTO);
            break;
         case MANUAL:
            session.setFlushMode(FlushMode.MANUAL);
            break;
         case COMMIT:
            session.setFlushMode(FlushMode.COMMIT);
            break;
      }
   }
   
   /**
    * The JNDI name of the Hibernate SessionFactory, if it is
    * to be obtained from JNDI
    */
   public String getSessionFactoryJndiName()
   {
      return sessionFactoryJndiName;
   }

   public void setSessionFactoryJndiName(String sessionFactoryName)
   {
      this.sessionFactoryJndiName = sessionFactoryName;
   }

   /**
    * A value binding expression that returns a SessionFactory,
    * if it is to be obtained as a Seam component reference
    */
   public void setSessionFactory(ValueExpression<SessionFactory> sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public ValueExpression<SessionFactory> getSessionFactory()
   {
      return sessionFactory;
   }

   /**
    * Hibernate filters to enable automatically
    */
   public List<Filter> getFilters()
   {
      return filters;
   }

   public void setFilters(List<Filter> filters)
   {
      this.filters = filters;
   }

   @Override
   public String toString()
   {
      return "ManagedHibernateSession(" + sessionFactoryJndiName + ")";
   }

}

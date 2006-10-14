//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.naming.NamingException;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.util.Naming;

/**
 * A Seam component that manages a conversation-scoped extended
 * persistence context that can be shared by arbitrary other
 * components.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
public class ManagedHibernateSession 
   implements Serializable, HttpSessionActivationListener, Mutable, PersistenceContextManager
{
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 3130309555079841107L;

   private static final Log log = LogFactory.getLog(ManagedHibernateSession.class);
   
   private Session session;
   private String sessionFactoryJndiName;
   private String componentName;
   private ValueBinding<SessionFactory> sessionFactory;
   
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
      createSession();
      
      PersistenceContexts.instance().touch(componentName);
      
      if ( log.isDebugEnabled() )
      {
         log.debug("created seam managed session for session factory: "+ sessionFactoryJndiName);
      }
   }

   private void createSession()
   {
      session = getSessionFactoryFromJndiOrValueBinding().openSession();
      setFlushMode( PersistenceContexts.instance().getFlushMode() );
   }
   
   @Unwrap
   public Session getSession()
   {
      session.isOpen();
      return session;
   }
   
   //we can't use @PrePassivate because it is intercept NEVER
   public void sessionWillPassivate(HttpSessionEvent event)
   {
      if ( !session.isDirty() )
      {
         session.close();
         session = null;
      }
   }
   
   //we can't use @PostActivate because it is intercept NEVER
   public void sessionDidActivate(HttpSessionEvent event)
   {
      if (session==null)
      {
         createSession();
      }
   }
   
   @Destroy
   public void destroy()
   {
      if ( log.isDebugEnabled() )
      {
         log.debug("destroying seam managed session for session factory: " + sessionFactoryJndiName);
      }
      session.close();
   }
   
   private SessionFactory getSessionFactoryFromJndiOrValueBinding()
   {
      if (sessionFactory==null)
      {
         try
         {
            return (SessionFactory) Naming.getInitialContext().lookup(sessionFactoryJndiName);
         }
         catch (NamingException ne)
         {
            throw new IllegalArgumentException("SessionFactory not found in JNDI", ne);
         }
      }
      else
      {
         SessionFactory result = sessionFactory.getValue();
         if (result==null)
         {
            throw new IllegalStateException("SessionFactory not found");
         }
         return result;
      }
   }
   
   public String getComponentName() {
      return componentName;
   }
   
   public void setFlushMode(FlushModeType flushMode)
   {
      switch (flushMode)
      {
         case AUTO:
            session.setFlushMode(FlushMode.AUTO);
            break;
         case MANUAL:
            session.setFlushMode(FlushMode.NEVER);
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
   public void setSessionFactory(ValueBinding<SessionFactory> sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public ValueBinding<SessionFactory> getSessionFactory()
   {
      return sessionFactory;
   }

   public String toString()
   {
      return "ManagedHibernateSession(" + sessionFactoryJndiName + ")";
   }

}

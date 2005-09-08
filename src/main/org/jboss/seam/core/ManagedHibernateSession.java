//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * A Seam component that manages a conversation-scoped extended
 * persistence context that can be shared by arbitrary other
 * components.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
public class ManagedHibernateSession implements Serializable
{
   private static final Logger log = Logger.getLogger(ManagedHibernateSession.class);
   
   private Session session;
   private String sessionFactoryName;
   
   @Create
   public void create(Component component)
   {
      sessionFactoryName = component.getName();
      try
      {
         session = getSessionFactory(sessionFactoryName).openSession();
      }
      catch (NamingException ne)
      {
         throw new IllegalArgumentException("SessionFactory not found", ne);
      }
      
      log.info("created seam managed session for session factory: "+ sessionFactoryName);
   }
   
   @Unwrap
   public Session getSession()
   {
      return session;
   }
   
   @Destroy
   public void destroy()
   {
      log.info("destroying seam managed session for session factory: " + sessionFactoryName);
      session.close();
   }
   
   private SessionFactory getSessionFactory(String persistenceUnit)
         throws NamingException
   {
      return (SessionFactory) new InitialContext().lookup(sessionFactoryName);
   }
   
   public String toString()
   {
      return "ManagedHibernateSession(" + sessionFactoryName + ")";
   }
}

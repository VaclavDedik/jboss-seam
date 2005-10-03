//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import org.jboss.seam.util.NamingHelper;

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
   /** The serialVersionUID */
   private static final long serialVersionUID = 3130309555079841107L;

   private static final Logger log = Logger.getLogger(ManagedHibernateSession.class);
   
   private Session session;
   private String sessionFactoryName;
   
   @Create
   public void create(Component component)
   {
      if (sessionFactoryName==null)
      {
         sessionFactoryName = component.getName();
      }
      try
      {
         session = getSessionFactory(sessionFactoryName).openSession();
      }
      catch (NamingException ne)
      {
         throw new IllegalArgumentException("SessionFactory not found", ne);
      }
      
      log.debug("created seam managed session for session factory: "+ sessionFactoryName);
   }
   
   @Unwrap
   public Session getSession()
   {
      return session;
   }
   
   @Destroy
   public void destroy()
   {
      log.debug("destroying seam managed session for session factory: " + sessionFactoryName);
      session.close();
   }
   
   private SessionFactory getSessionFactory(String persistenceUnit)
         throws NamingException
   {
      InitialContext initialContext = NamingHelper.getInitialContext();
      return (SessionFactory) initialContext.lookup(sessionFactoryName);
   }
   
   public String toString()
   {
      return "ManagedHibernateSession(" + sessionFactoryName + ")";
   }

   public String getSessionFactoryName()
   {
      return sessionFactoryName;
   }

   public void setSessionFactoryName(String sessionFactoryName)
   {
      this.sessionFactoryName = sessionFactoryName;
   }
   
   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
   {
      //TODO: this is just noise! We should deprecate disconnect/reconnect in HB core.
      ois.defaultReadObject();
      if (session!=null && !session.isConnected() ) session.reconnect();
   }

   private void writeObject(ObjectOutputStream oos) throws IOException
   {
      //TODO: this is just noise! We should deprecate disconnect/reconnect in HB core.
      if (session!=null && session.isConnected() ) session.disconnect();
      oos.defaultWriteObject();
   }
}

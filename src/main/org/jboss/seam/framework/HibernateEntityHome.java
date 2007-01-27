package org.jboss.seam.framework;

import java.io.Serializable;

import org.hibernate.Session;
import org.jboss.seam.annotations.Transactional;

/**
 * Base class for Home objects for Hibernate entities.
 * 
 * @author Gavin King
 *
 */
public class HibernateEntityHome<E> extends Home<Session, E>
{
   
   private static final long serialVersionUID = 6071072408602519385L;
   
   @Override
   public void create()
   {
      super.create();
      if ( getSession()==null )
      {
         throw new IllegalStateException("session is null");
      }
   }
   
   @Transactional
   public boolean isManaged()
   {
      return getInstance()!=null && 
            getSession().contains( getInstance() );
   }
   
   @Transactional
   public String update()
   {
      getSession().flush();
      updatedMessage();
      return "updated";
   }
   
   @Transactional
   public String persist()
   {
      getSession().persist( getInstance() );
      getSession().flush();
      assignId( getSession().getIdentifier( getInstance() ) );
      createdMessage();
      return "persisted";
   }
   
   @Transactional
   public String remove()
   {
      getSession().delete( getInstance() );
      getSession().flush();
      deletedMessage();
      return "removed";
   }
   
   @Transactional
   @Override
   public E find()
   {
      E result = (E) getSession().get( getEntityClass(), (Serializable) getId() );
      if (result==null) result = handleNotFound();
      return result;
   }
   
   public Session getSession()
   {
      return getPersistenceContext();
   }
   
   public void setSession(Session session)
   {
      setPersistenceContext(session);
   }
   
}

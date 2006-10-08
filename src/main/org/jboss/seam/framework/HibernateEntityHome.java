package org.jboss.seam.framework;

import java.io.Serializable;

import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.FacesMessages;

public class HibernateEntityHome<E> extends Home<E>
{
   private Session session;

   @In(create=true) 
   private FacesMessages facesMessages; 
   
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
      facesMessages.add( getUpdatedMessage() );
      return "updated";
   }
   
   @Transactional
   public String persist()
   {
      getSession().persist( getInstance() );
      getSession().flush();
      setId( getSession().getIdentifier( getInstance() ) );
      facesMessages.add( getCreatedMessage() );
      return "persisted";
   }

   @Transactional
   public String remove()
   {
      getSession().delete( getInstance() );
      getSession().flush();
      facesMessages.add( getDeletedMessage() );
      return "removed";
   }
   
   @Transactional
   public E find()
   {
      E result = (E) getSession().get( getEntityClass(), (Serializable) getId() );
      if (result==null) result = handleNotFound();
      return result;
   }

   public Session getSession()
   {
      return session;
   }

   public void setSession(Session session)
   {
      this.session = session;
   }

}

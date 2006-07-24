package org.jboss.seam.core;

import java.io.Serializable;

import org.hibernate.Session;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;

public class ManagedHibernateEntity
{
   private Session session;
   private Serializable id;
   private String entityClass;
   
   public Session getSession()
   {
      return session;
   }

   public void setSession(Session session)
   {
      this.session = session;
   }

   public Serializable getId()
   {
      return id;
   }

   public void setId(Serializable id)
   {
      this.id = id;
   }
   
   public String getEntityClass()
   {
      return entityClass;
   }

   public void setEntityClass(String entityClass)
   {
      this.entityClass = entityClass;
   }

   @Unwrap @Transactional
   public Object getInstance() throws ClassNotFoundException
   {
      Class clazz = Class.forName(entityClass);
      return session.get(clazz, id);
   }

}

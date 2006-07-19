package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import org.hibernate.Session;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Unwrap;

@Intercept(NEVER)
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

   @Unwrap
   public Object getInstance() throws ClassNotFoundException
   {
      Class clazz = Class.forName(entityClass);
      return session.get(clazz, id);
   }

}

package org.jboss.seam.framework;

import java.io.Serializable;

import org.hibernate.Session;

public class HibernateEntityIdentifier extends Identifier<Session>
{

   public HibernateEntityIdentifier(Object entity, Session session)
   {
      super(entity.getClass(), session.getIdentifier(entity));
   }
   
   public HibernateEntityIdentifier(Class clazz, Object id)
   {
      super(clazz, id);
   }
   
   @Override
   public Object find(Session session)
   {
      if (session == null)
      {
         throw new IllegalArgumentException("session must not be null");
      }
      return session.get(getClazz(), (Serializable) getId());
   }

}

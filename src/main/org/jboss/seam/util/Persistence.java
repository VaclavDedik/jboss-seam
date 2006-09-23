package org.jboss.seam.util;

import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Session;

public class Persistence
{

   public static void setFlushModeManual(EntityManager entityManager)
   {
      if (entityManager instanceof Session)
      {
         ( (Session) entityManager.getDelegate() ).setFlushMode(FlushMode.NEVER);
      }
      else
      {
         throw new IllegalArgumentException("FlushMode.MANUAL only supported for Hibernate EntityManager");
      }
   }

   public static boolean isDirty(EntityManager entityManager)
   {
      if (entityManager instanceof Session)
      {
         return ( (Session) entityManager.getDelegate() ).isDirty();
      }
      else
      {
         return true; //best we can do!
      }
   }

}

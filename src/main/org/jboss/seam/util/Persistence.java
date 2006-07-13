package org.jboss.seam.util;

import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Session;

public class Persistence
{

   public static void setFlushModeManual(EntityManager entityManager)
   {
      ( (Session) entityManager.getDelegate() ).setFlushMode(FlushMode.NEVER);
   }

}

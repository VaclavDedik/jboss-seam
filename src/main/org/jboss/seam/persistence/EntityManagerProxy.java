package org.jboss.seam.persistence;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.security.permission.PermissionManager;
import org.jboss.seam.util.DelegatingInvocationHandler;

/**
 * Proxies the EntityManager, and implements EL interpolation in JPA-QL
 * 
 * @author Gavin King
 * @author Shane Bryzak
 */
public class EntityManagerProxy extends DelegatingInvocationHandler<EntityManager> implements Serializable
{
   public EntityManagerProxy(EntityManager entityManager)
   {
      super(entityManager);
   }

   public Query createQuery(String ejbql)
   {
      if ( ejbql.indexOf('#')>0 )
      {
         QueryParser qp = new QueryParser(ejbql);
         Query query = super.getDelegate().createQuery( qp.getEjbql() );
         for (int i=0; i<qp.getParameterValueBindings().size(); i++)
         {
            query.setParameter( 
                     QueryParser.getParameterName(i), 
                     qp.getParameterValueBindings().get(i).getValue() 
                  );
         }
         return query;
      }
      else
      {
         return super.getDelegate().createQuery(ejbql);
      }
   }

 /*  public Object getDelegate()
   {
      return PersistenceProvider.instance().proxyDelegate( super.getDelegate().getDelegate() );
   }*/


   public void remove(Object entity)
   {
      super.getDelegate().remove(entity);
      PermissionManager.instance().clearPermissions(entity);
   }
}

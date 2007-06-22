package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * Support for JPA EntityTransaction API
 * 
 * @author Gavin King
 * 
 */
@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.STATELESS)
@Install(value=false, precedence=FRAMEWORK)
@BypassInterceptors
public class EntityTransaction extends Transaction
{

   private ValueExpression<EntityManager> entityManager;
   
   @Unwrap
   @Override
   public UserTransaction getTransaction() throws NamingException
   {
      EntityManager em = entityManager.getValue();
      if ( em==null )
      {
         return createNoTransaction();
      }
      else
      {
         return createETTransaction(em);
      }
   }

   protected UserTransaction createETTransaction(EntityManager em)
   {
      return new ETTransaction( em.getTransaction() );
   }

   public ValueExpression<EntityManager> getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(ValueExpression<EntityManager> entityManager)
   {
      this.entityManager = entityManager;
   }

}

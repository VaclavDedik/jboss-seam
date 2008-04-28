package org.jboss.seam.security.permission;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.persistence.PersistenceProvider;

/**
 * An Identifier strategy for entity-based permission checks
 * 
 * @author Shane Bryzak
 */
public class EntityIdentifierStrategy implements IdentifierStrategy
{
   private ValueExpression<EntityManager> entityManager;   
   
   private PersistenceProvider persistenceProvider;
   
   public EntityIdentifierStrategy()
   {
      persistenceProvider = (PersistenceProvider) Component.getInstance(PersistenceProvider.class, true);
      
      if (entityManager == null)
      {
         entityManager = Expressions.instance().createValueExpression("#{entityManager}", 
               EntityManager.class);
      }         
   }
   
   public boolean canIdentify(Class targetClass)
   {
      return targetClass.isAnnotationPresent(Entity.class);
   }

   public String getIdentifier(Object target)
   {
      // TODO temporary, need to implement properly
      return target.getClass().getName() + ":" + persistenceProvider.getId(target, lookupEntityManager());
   }

   private EntityManager lookupEntityManager()
   {
      return entityManager.getValue();
   }
}

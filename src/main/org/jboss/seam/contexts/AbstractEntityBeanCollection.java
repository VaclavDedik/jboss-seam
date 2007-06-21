package org.jboss.seam.contexts;

abstract class AbstractEntityBeanCollection implements Wrapper
{
   public final void activate()
   {
      if ( isPassivatedEntitiesInitialized() && isAnyVersioned() )
      {
         activateAll();
      }
   }

   public final Object getInstance()
   {
      if ( isPassivatedEntitiesInitialized() && !isAnyVersioned() )
      {
         activateAll();
      }
      return getEntityCollection();
   }
   
   public final boolean passivate()
   {
      if ( PassivatedEntity.isTransactionRolledBackOrMarkedRollback() )
      {
         clearPassivatedEntities();
      }
      else
      {
         passivateAll();
      }
      return true;
   }

   private boolean isAnyVersioned()
   {
      for ( PassivatedEntity passivatedEntity: getPassivatedEntities() )
      {
         if ( passivatedEntity!=null && passivatedEntity.isVersioned() ) return true;
      }
      return false;
   }
   
   protected abstract void activateAll();
   protected abstract void passivateAll();
   protected abstract Iterable<PassivatedEntity> getPassivatedEntities();
   protected abstract void clearPassivatedEntities();
   protected abstract boolean isPassivatedEntitiesInitialized();
   protected abstract Object getEntityCollection();

}

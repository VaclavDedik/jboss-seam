package org.jboss.seam.contexts;

import org.jboss.seam.core.Mutable;

public class EntityBean implements Mutable
{
   private Object instance;
   private PassivatedEntity passivatedEntity;

   public EntityBean(Object instance)
   {
      this.instance = instance;
   }

   //TODO: use @Unwrap
   public Object getInstance()
   {
      if (passivatedEntity==null)
      {
         return instance;
      }
      else
      {
         return passivatedEntity.toEntityReference();
      }
   }
   
   public boolean clearDirty()
   {
      if (passivatedEntity==null)
      {
         if ( !PassivatedEntity.isTransactionMarkedRollback() )
         {
            passivatedEntity = PassivatedEntity.createPassivatedEntity(instance, null);
            if (passivatedEntity!=null)
            {
               instance = null;
            }
         }
         return true;
      }
      else
      {
         return false;
      }
   }
}

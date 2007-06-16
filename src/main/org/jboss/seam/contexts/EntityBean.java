package org.jboss.seam.contexts;


/**
 * Swizzles entities held in the conversation context at
 * the end of each request.
 * 
 * @see PassivatedEntity
 * 
 * @author Gavin King
 *
 */
public class EntityBean implements Wrapper
{
   private static final long serialVersionUID = -2884601453783925804L;
   
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
         if ( !PassivatedEntity.isTransactionRolledBackOrMarkedRollback() )
         {
            passivatedEntity = PassivatedEntity.createPassivatedEntity(instance);
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

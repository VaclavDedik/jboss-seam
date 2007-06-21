package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Swizzles entities held in the conversation context at
 * the end of each request.
 * 
 * @see PassivatedEntity
 * 
 * @author Gavin King
 *
 */
class EntityBeanSet implements Wrapper
{
   private static final long serialVersionUID = -2884601453783925804L;
   
   private Set instance;
   private List<PassivatedEntity> passivatedEntityList;
   
   public EntityBeanSet(Set instance)
   {
      this.instance = instance;
   }
   
   public void activate()
   {
      if (passivatedEntityList!=null)
      {
         for ( PassivatedEntity pe: passivatedEntityList )
         {
            instance.add( pe.toEntityReference(true) );
         }
         passivatedEntityList = null;
      }
   }
   
   //TODO: use @Unwrap
   public Object getInstance()
   {
      return instance;
   }
   
   public boolean passivate()
   {
      if ( !PassivatedEntity.isTransactionRolledBackOrMarkedRollback() )
      {
         passivatedEntityList = new ArrayList<PassivatedEntity>( instance.size() );
         boolean found = false;
         for ( Object value: instance )
         {
            if (value!=null)
            {
               PassivatedEntity passivatedEntity = PassivatedEntity.passivateEntity(value);
               if (passivatedEntity!=null)
               {
                  if (!found) instance = new HashSet(instance);
                  found=true;
                  //this would be dangerous, except that we 
                  //are doing it to a copy of the original 
                  //list:
                  instance.remove(value);
                  passivatedEntityList.add(passivatedEntity);
               }
            }
         }
         if (!found) passivatedEntityList=null;
      }
      return true;
   }
   
}

package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.List;

/**
 * Swizzles entities held in the conversation context at
 * the end of each request.
 * 
 * @see PassivatedEntity
 * 
 * @author Gavin King
 *
 */
public class EntityBeanList implements Wrapper
{
   private static final long serialVersionUID = -2884601453783925804L;
   
   private List instance;
   private List<PassivatedEntity> passivatedEntityList;
   
   public EntityBeanList(List instance)
   {
      this.instance = instance;
   }
   
   //TODO: use @Unwrap
   public Object getInstance()
   {
      if (passivatedEntityList!=null)
      {
         for (int i=0; i<passivatedEntityList.size(); i++)
         {
            PassivatedEntity passivatedEntity = passivatedEntityList.get(i);
            if (passivatedEntity!=null)
            {
               instance.set( i, passivatedEntity.toEntityReference() );
            }
         }
         passivatedEntityList = null;
      }
      return instance;
   }
   
   public boolean clearDirty()
   {
      if ( !PassivatedEntity.isTransactionRolledBackOrMarkedRollback() )
      {
         passivatedEntityList = new ArrayList<PassivatedEntity>( instance.size() );
         boolean found = false;
         for (int i=0; i<instance.size(); i++ )
         {
            PassivatedEntity passivatedEntity = PassivatedEntity.createPassivatedEntity( instance.get(i) );
            if (passivatedEntity!=null)
            {
               if (!found) instance = new ArrayList(instance);
               found=true;
               //this would be dangerous, except that we 
               //are doing it to a copy of the original 
               //list:
               instance.set(i, null); 
            }
            passivatedEntityList.add(passivatedEntity);
         }
         if (!found) passivatedEntityList=null;
      }
      return true;
   }
   
}

package org.jboss.seam.contexts;

import java.util.HashMap;
import java.util.Map;
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
public class EntityBeanMap implements Wrapper
{
   private static final long serialVersionUID = -2884601453783925804L;
   
   private Map instance;
   private Map<Object, PassivatedEntity> passivatedEntityMap;
   
   public EntityBeanMap(Map instance)
   {
      this.instance = instance;
   }
   
   //TODO: use @Unwrap
   public Object getInstance()
   {
      if (passivatedEntityMap!=null)
      {
         for ( Map.Entry<Object, PassivatedEntity> me: passivatedEntityMap.entrySet() )
         {
            instance.put( me.getKey(), me.getValue().toEntityReference() );
         }
         passivatedEntityMap = null;
      }
      return instance;
   }
   
   public boolean clearDirty()
   {
      if ( !PassivatedEntity.isTransactionRolledBackOrMarkedRollback() )
      {
         passivatedEntityMap = new HashMap<Object, PassivatedEntity>( instance.size() );
         boolean found = false;
         for ( Map.Entry me: (Set<Map.Entry>) instance.entrySet() )
         {
            Object value = me.getValue();
            if (value!=null)
            {
               PassivatedEntity passivatedEntity = PassivatedEntity.createPassivatedEntity(value);
               if (passivatedEntity!=null)
               {
                  if (!found) instance = new HashMap(instance);
                  found=true;
                  //this would be dangerous, except that we 
                  //are doing it to a copy of the original 
                  //list:
                  instance.remove( me.getKey() ); 
                  passivatedEntityMap.put( me.getKey(), passivatedEntity );
               }
            }
         }
         if (!found) passivatedEntityMap=null;
      }
      return true;
   }
   
}

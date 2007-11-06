package org.jboss.seam.ui.converter.entityConverter;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.Identifier;
import org.jboss.seam.framework.PersistenceController;

/**
 * Helper class to load entities for the entity converter
 * @author Pete Muir
 *
 */
public abstract class AbstractEntityLoader<T> extends PersistenceController<T>
{
   
   /**
    * Load and return the entity stored
    * @param key
    * @return The entity or null if no entity is available at that key
    */
   @Transactional
   public Object get(Integer key)
   {
      Identifier identifier = EntityIdentifierStore.instance().get(key);
      if (identifier != null)
      {
         return identifier.find(getPersistenceContext());
      }
      else
      {
         return null;
      }
   }

   /**
    * Store an entity id/clazz
    * @param entity The entity to store
    * @return The key under which the clazz/id are stored
    */
   @Transactional
   public Integer put(Object entity)
   {      
      return EntityIdentifierStore.instance().put(createIdentifier(entity));
   }
   
   protected abstract Identifier createIdentifier(Object entity);

}
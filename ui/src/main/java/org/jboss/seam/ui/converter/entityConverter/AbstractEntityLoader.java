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
   public Object get(String key)
   {
      if (getPersistenceContext() == null)
      {
         throw new IllegalStateException("Unable to get a Persistence Context to load Entity. Make sure you have an SMPC called entityManager configured in components.xml (or have correctly configured s:convertEntity to use another SMPC).");
      }
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
   public String put(Object entity)
   {
      if (getPersistenceContext() == null)
      {
         throw new IllegalStateException("Unable to get a Persistence Context to store Entity. Make sure you have an SMPC called entityManager configured in components.xml (or have correctly configured s:convertEntity to use another SMPC).");
      }
      return EntityIdentifierStore.instance().put(createIdentifier(entity), entity);
   }
   
   protected abstract Identifier createIdentifier(Object entity);

   public abstract void validate();

}
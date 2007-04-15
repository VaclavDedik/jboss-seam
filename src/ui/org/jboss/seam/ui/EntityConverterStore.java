package org.jboss.seam.ui;

import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.ScopeType.PAGE;
import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityIdentifier;
import org.jboss.seam.framework.MutableEntityController;

/**
 * Stores entity identifiers under a key, which can be used on a page
 *
 */

@Name("org.jboss.seam.ui.entityConverterStore")
@Install(precedence=BUILT_IN)
@Scope(PAGE)
@Intercept(NEVER)
public class EntityConverterStore extends MutableEntityController
{
   
   private List<EntityIdentifier> store = new ArrayList<EntityIdentifier>();
   
   /**
    * Load and return the entity stored
    * @param key
    * @return The entity or null if no entity is available at that key
    */
   @Transactional
   public Object get(Integer key)
   {
      try
      {
         return store.get(key).find(getEntityManager());
      }
      catch (IndexOutOfBoundsException e)
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
      EntityIdentifier key = new EntityIdentifier(entity, getEntityManager());
      if (!store.contains(key))
      {
         store.add(key);
         setDirty();
      }
      return store.indexOf(key);
   }
   
   public static EntityConverterStore instance() 
   {
      if (!Contexts.isPageContextActive())
      {
         throw new IllegalArgumentException("Page scope not active");
      }
      return (EntityConverterStore) Component.getInstance(EntityConverterStore.class);
   }
}

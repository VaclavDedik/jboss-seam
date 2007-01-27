package org.jboss.seam.framework;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Base class for controller objects which require a persistence
 * context object.
 * 
 * @author Gavin King
 *
 * @param <T> the persistence context class (eg. Session or EntityManager)
 */
public abstract class PersistenceController<T> extends Controller
{
   private transient T persistenceContext;
   private String persistenceContextName;
   
   public T getPersistenceContext()
   {
      if (persistenceContext==null)
      {
         persistenceContext = (T) getComponentInstance( getPersistenceContextName() );
      }
      return persistenceContext;
   }

   public void setPersistenceContext(T persistenceContext)
   {
      this.persistenceContext = persistenceContext;
   }

   protected String getPersistenceContextName()
   {
      if (persistenceContextName==null)
      {
         Type type = getClass().getGenericSuperclass();
         if (type instanceof ParameterizedType)
         {
            ParameterizedType paramType = (ParameterizedType) type;
            String className = ( (Class<T>) paramType.getActualTypeArguments()[0] ).getName();
            int loc = className.lastIndexOf('.');
            persistenceContextName = className.substring(loc+1,loc+2).toLowerCase() + className.substring(loc+2);
         }
         else
         {
            throw new IllegalArgumentException("Could not guess persistence context class by reflection");
         }
      }
      return persistenceContextName;
   }

}

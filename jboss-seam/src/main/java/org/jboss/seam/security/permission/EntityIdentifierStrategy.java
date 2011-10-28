package org.jboss.seam.security.permission;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.security.permission.Identifier;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.util.Strings;

/**
 * An Identifier strategy for entity-based permission checks
 * 
 * @author Shane Bryzak
 */
public class EntityIdentifierStrategy implements IdentifierStrategy, Serializable
{

   private static final long serialVersionUID = 12456789L;

   private transient ValueExpression<EntityManager> entityManager;   
   
   private transient PersistenceProvider persistenceProvider;
   
   private Map<Class <?>,String> identifierNames = new ConcurrentHashMap<Class <?>,String>();

   public void init()
   {
      if (persistenceProvider == null)
      {
         persistenceProvider = (PersistenceProvider) Component.getInstance(PersistenceProvider.class, true);
      }

      if (entityManager == null)
      {
         entityManager = Expressions.instance().createValueExpression("#{entityManager}", EntityManager.class);
      }

   }

   public EntityIdentifierStrategy()
   {
      init();   
   }
   
   public boolean canIdentify(Class<?> targetClass)
   {
      return targetClass.isAnnotationPresent(Entity.class);
   }

   public String getIdentifier(Object target)
   {
      if(persistenceProvider == null) init();
      Object persProviderId = persistenceProvider.getId(target, lookupEntityManager()).toString();
       return String.format("%s:%s", getIdentifierName(target.getClass()),  persProviderId);
   }
   
   private String getIdentifierName(Class<? extends Object> cls)
   {
      if (!identifierNames.containsKey(cls))
      {   
         String name = null;
         
         if (cls.isAnnotationPresent(Identifier.class))
         {
            Identifier identifier = cls.getAnnotation(Identifier.class);
            if ( !Strings.isEmpty(identifier.name()) )
            {
               name = identifier.name();
            }
         }
         
         if (name == null)
         {
            name = Seam.getComponentName(cls);
         }
         
         if (name == null)
         {
            name = cls.getName().substring(cls.getName().lastIndexOf('.') + 1);
         }
         
         identifierNames.put(cls, name);
         return name;
      }
      
      return identifierNames.get(cls);
   }

   private EntityManager lookupEntityManager()
   {
      if(entityManager == null) init();
      return entityManager.getValue();
   }
}

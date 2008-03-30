package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.util.Strings;

/**
 * Entity permission checks
 * 
 * @author Shane Bryzak
 * @author Pete Muir
 */
@Name("org.jboss.seam.security.entityPermissionChecker")
@Scope(STATELESS)
@Install(precedence = BUILT_IN, classDependencies={"javax.persistence.EntityManager"})
@BypassInterceptors
public class EntityPermissionChecker
{

   public static EntityPermissionChecker instance()
   {
      return (EntityPermissionChecker) Component.getInstance(EntityPermissionChecker.class, STATELESS);
   }
   
   protected Method getProtectedMethod(EntityAction action, Object bean, EntityManager entityManager)
   {
      if (bean != null)
      {
         switch (action)
         {
         case READ:
            return PersistenceProvider.instance().getPostLoadMethod(bean, entityManager);
            
         case INSERT:
            return PersistenceProvider.instance().getPrePersistMethod(bean, entityManager);
            
         case UPDATE:
            return PersistenceProvider.instance().getPreUpdateMethod(bean, entityManager);
            
         case DELETE:
            return PersistenceProvider.instance().getPreRemoveMethod(bean, entityManager);
         }

      }
      return null;
   }
   
   public void checkEntityPermission(Object entity, EntityAction action)
   {
      checkEntityPermission(entity, action, getProtectedMethod(action, entity, null));
   }
   
   public void checkEntityPermission(Object entity, EntityAction action, EntityManager entityManager)
   {
      checkEntityPermission(entity, action, getProtectedMethod(action, entity, entityManager));
   }
   
   protected void checkEntityPermission(Object entity, EntityAction action, Method m)
   {
      if (entity != null)
      {
         if (!Identity.isSecurityEnabled())
            return;
   
         Identity identity = Identity.instance();
   
         identity.isLoggedIn(true);
         
         Class beanClass = PersistenceProvider.instance().getBeanClass(entity);
         
         Restrict restrict = null;
   
         if (m != null && m.isAnnotationPresent(Restrict.class))
         {
            restrict = m.getAnnotation(Restrict.class);
         }
         else if (entity.getClass().isAnnotationPresent(Restrict.class))
         {
            restrict = entity.getClass().getAnnotation(Restrict.class);
         }
   
         if (restrict != null)
         {
            if (Strings.isEmpty(restrict.value()))
            {
               String name = Seam.getComponentName(beanClass);
               if (name == null)
               {
                  name = beanClass.getName();
               }
               Identity.instance().checkPermission(name, action.toString(), entity);
            }
            else
            {
               Identity.instance().checkRestriction(restrict.value());
            }
         }
      }
   }
   
}

package org.jboss.seam.security.permission.dynamic;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionStore;

/**
 * A permission store implementation that uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@BypassInterceptors
public class JpaDynamicPermissionStore implements PermissionStore, Serializable
{
   private String entityManagerName = "entityManager";
   
   private Class permissionClass;   
   
   public boolean grantPermission(Permission permission)
   {
      try
      {
         if (permissionClass == null)
         {
            throw new RuntimeException("Could not grant permission, permissionClass not set");
         }
                 
         Object instance = permissionClass.newInstance();
//         instance.setTarget(permission.getTarget());
//         instance.setAction(permission.getAction());
//         instance.setAccount(permission.getRecipient());

         getEntityManager().persist(instance);
         
         return true;
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Could not grant permission", ex);
      }   
   }
   
   public boolean revokePermission(Permission permission)
   {
      try
      {
         EntityManager em = getEntityManager();
         
         Object instance = em.createQuery(
            "from " + permissionClass.getName() +
            " where target = :target and action = :action and account = :account " +
            " and accountType = :accountType")
            .setParameter("target", permission.getTarget())
            .setParameter("action", "action")
            .setParameter("account", permission.getRecipient())
            .getSingleResult();
         
         em.remove(instance);
         return true;
      }
      catch (NoResultException ex)
      {
         return false;
      }
   }   

   public List<Permission> listPermissions(Object target, String action) 
   {
      return getEntityManager().createQuery(
            "from " + permissionClass.getName() + 
            " where target = :target and action = :action")
            .setParameter("target", target)
            .setParameter("action", action)
            .getResultList();
   }

   public List<Permission> listPermissions(Object target) 
   {
      return getEntityManager().createQuery(
            "from " + permissionClass.getName() + " where target = :target")
            .setParameter("target", target)
            .getResultList();
   }

   private EntityManager getEntityManager()
   {
      return (EntityManager) Component.getInstance(entityManagerName);
   }
   
   public String getEntityManagerName()
   {
      return entityManagerName;
   }
   
   public void setEntityManagerName(String name)
   {
      this.entityManagerName = name;
   }      
   
   public Class getPermissionClass()
   {
      return permissionClass;
   }
   
   public void setPermissionClass(Class permissionClass)
   {
      this.permissionClass = permissionClass;
   }
}

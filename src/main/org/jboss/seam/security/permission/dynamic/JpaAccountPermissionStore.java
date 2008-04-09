package org.jboss.seam.security.permission.dynamic;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.permission.AccountType;

/**
 * A permission store implementation that uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@BypassInterceptors
public class JpaAccountPermissionStore implements AccountPermissionStore, Serializable
{
   private String entityManagerName = "entityManager";
   
   private Class<? extends AccountPermission> permissionClass;   
   
   public boolean grantPermission(String target, String action, String account,
         AccountType accountType) 
   {
      try
      {
         if (permissionClass == null)
         {
            throw new RuntimeException("Could not grant permission, permissionClass not set");
         }
                 
         AccountPermission permission = permissionClass.newInstance();
         permission.setTarget(target);
         permission.setAction(action);
         permission.setAccount(account);
         permission.setAccountType(accountType);

         getEntityManager().persist(permission);
         
         return true;
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Could not grant permission", ex);
      }   
   }
   
   public boolean revokePermission(String target, String action,
         String account, AccountType accountType) 
   {
      try
      {
         EntityManager em = getEntityManager();
         
         AccountPermission permission = (AccountPermission) em.createQuery(
            "from " + permissionClass.getName() +
            " where target = :target and action = :action and account = :account " +
            " and accountType = :accountType")
            .setParameter("target", target)
            .setParameter("action", "action")
            .setParameter("account", account)
            .setParameter("accountType", accountType)
            .getSingleResult();
         
         em.remove(permission);
         return true;
      }
      catch (NoResultException ex)
      {
         return false;
      }
   }   

   public List<AccountPermission> listPermissions(String target, String action) 
   {
      return getEntityManager().createQuery(
            "from " + permissionClass.getName() + 
            " where target = :target and action = :action")
            .setParameter("target", target)
            .setParameter("action", action)
            .getResultList();
   }

   public List<AccountPermission> listPermissions(String target) 
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

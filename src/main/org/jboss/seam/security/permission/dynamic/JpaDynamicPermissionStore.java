package org.jboss.seam.security.permission.dynamic;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.security.permission.PermissionAction;
import org.jboss.seam.annotations.security.permission.PermissionDiscriminator;
import org.jboss.seam.annotations.security.permission.PermissionRole;
import org.jboss.seam.annotations.security.permission.PermissionTarget;
import org.jboss.seam.annotations.security.permission.PermissionUser;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.management.BeanProperty;
import org.jboss.seam.security.management.IdentityManagementException;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionStore;

/**
 * A permission store implementation that uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.permission.jpaDynamicPermissionStore")
@Install(precedence = BUILT_IN, value=false) 
@Scope(APPLICATION)
@BypassInterceptors
public class JpaDynamicPermissionStore implements PermissionStore, Serializable
{
   private static final LogProvider log = Logging.getLogProvider(JpaDynamicPermissionStore.class); 
   
   private ValueExpression<EntityManager> entityManager;
   
   private Class userPermissionClass;
   private Class rolePermissionClass;
   
   private BeanProperty userProperty;
   private BeanProperty roleProperty;
   
   private BeanProperty targetProperty;
   private BeanProperty actionProperty;   
   private BeanProperty discriminatorProperty;
   
   private BeanProperty roleTargetProperty;
   private BeanProperty roleActionProperty;
   
   @Create
   public void init()
   {      
      if (userPermissionClass == null)
      {
         log.debug("No permissionClass set, JpaDynamicPermissionStore will be unavailable.");
         return;
      }   
      
      if (entityManager == null)
      {
         entityManager = Expressions.instance().createValueExpression("#{entityManager}", EntityManager.class);
      }       
      
      initProperties();
   }   
   
   private void initProperties()
   {
      userProperty = BeanProperty.scanForProperty(userPermissionClass, PermissionUser.class);
      targetProperty = BeanProperty.scanForProperty(userPermissionClass, PermissionTarget.class);
      actionProperty = BeanProperty.scanForProperty(userPermissionClass, PermissionAction.class);
      
      if (rolePermissionClass != null)
      {
         roleProperty = BeanProperty.scanForProperty(rolePermissionClass, PermissionRole.class);
         if (roleProperty != null)
         {
            roleTargetProperty = BeanProperty.scanForProperty(rolePermissionClass, PermissionTarget.class);
            roleActionProperty = BeanProperty.scanForProperty(rolePermissionClass, PermissionAction.class);
         }
      }
      else
      {
         roleProperty = BeanProperty.scanForProperty(userPermissionClass, PermissionRole.class);
         if (roleProperty != null)
         {
            discriminatorProperty = BeanProperty.scanForProperty(userPermissionClass, PermissionDiscriminator.class);
         }
      }
      
      if (userProperty == null) 
      {
         throw new IdentityManagementException("Invalid userPermissionClass " + userPermissionClass.getName() + 
               " - required annotation @PermissionUser not found on any Field or Method.");
      }

      // TODO additional validation checks for both permission classes
   }   
   
   public boolean grantPermission(Permission permission)
   {
      try
      {
         if (userPermissionClass == null)
         {
            throw new RuntimeException("Could not grant permission, permissionClass not set");
         }
                 
         Object instance = userPermissionClass.newInstance();
//         instance.setTarget(permission.getTarget());
//         instance.setAction(permission.getAction());
//         instance.setAccount(permission.getRecipient());

         lookupEntityManager().persist(instance);
         
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
         EntityManager em = lookupEntityManager();
         
         Object instance = em.createQuery(
            "from " + userPermissionClass.getName() +
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
      return lookupEntityManager().createQuery(
            "from " + userPermissionClass.getName() + 
            " where target = :target and action = :action")
            .setParameter("target", target)
            .setParameter("action", action)
            .getResultList();
   }

   public List<Permission> listPermissions(Object target) 
   {
      return lookupEntityManager().createQuery(
            "from " + userPermissionClass.getName() + " where target = :target")
            .setParameter("target", target)
            .getResultList();
   }

   private EntityManager lookupEntityManager()
   {
      return entityManager.getValue();
   }
   
   public ValueExpression getEntityManager()
   {
      return entityManager;
   }
   
   public void setEntityManager(ValueExpression expression)
   {
      this.entityManager = expression;
   } 
   
   public Class getUserPermissionClass()
   {
      return userPermissionClass;
   }
   
   public void setUserPermissionClass(Class userPermissionClass)
   {
      this.userPermissionClass = userPermissionClass;
   }
   
   public Class getRolePermissionClass()
   {
      return rolePermissionClass;
   }
   
   public void setRolePermissionClass(Class rolePermissionClass)
   {
      this.rolePermissionClass = rolePermissionClass;
   }
}

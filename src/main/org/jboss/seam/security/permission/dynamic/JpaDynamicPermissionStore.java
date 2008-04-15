package org.jboss.seam.security.permission.dynamic;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.Component;
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
import org.jboss.seam.security.Role;
import org.jboss.seam.security.management.BeanProperty;
import org.jboss.seam.security.management.JpaIdentityStore;
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
   
   private String selectUserPermissionQuery;
   private String selectRolePermissionQuery;

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
      buildQueries();
   }   
   
   protected void initProperties()
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
         throw new RuntimeException("Invalid userPermissionClass " + userPermissionClass.getName() + 
               " - required annotation @PermissionUser not found on any Field or Method.");
      }

      if (rolePermissionClass != null)
      {
         if (roleProperty == null)
         {
            throw new RuntimeException("Invalid rolePermissionClass " + rolePermissionClass.getName() +
                  " - required annotation @PermissionRole not found on any Field or Method.");
         }
         
         if (roleTargetProperty == null)
         {
            throw new RuntimeException("Invalid rolePermissionClass " + rolePermissionClass.getName() +
                  " - required annotation @PermissionTarget not found on any Field or Method.");
         }
         
         if (roleActionProperty == null)
         {
            throw new RuntimeException("Invalid rolePermissionClass " + rolePermissionClass.getName() +
                  " - required annotation @PermissionAction not found on any Field or Method.");
         }
      }
      else if (discriminatorProperty == null)
      {
         throw new RuntimeException("Invalid userPermissionClass " + rolePermissionClass.getName() +
               " - no rolePermissionClass set and @PermissionDiscriminator annotation not found on " +
               "any Field or Method");
      }
   }   
   
   protected void buildQueries()
   {
      StringBuffer query = new StringBuffer();
      query.append("select p from ");
      query.append(userPermissionClass.getName());
      query.append(" p where ");
      query.append(targetProperty.getName());
      query.append(" = :target and ");
      query.append(actionProperty.getName());
      query.append(" = :action and ");
      query.append(userProperty.getName());
      query.append(" = :recipient");
      
      if (rolePermissionClass == null)
      {
         query.append(" and ");
         query.append(discriminatorProperty.getName());
         query.append(" = :discriminator");         
      }
            
      selectUserPermissionQuery = query.toString();
      
      if (rolePermissionClass != null)
      {
         query.setLength(0);
         query.append("select p from ");
         query.append(rolePermissionClass.getName());
         query.append(" p where ");
         query.append(roleTargetProperty.getName());
         query.append(" = :target and ");
         query.append(roleActionProperty.getName());
         query.append(" = :action and ");
         query.append(roleProperty.getName());
         query.append(" = :recipient");
         
         selectRolePermissionQuery = query.toString();
      }
      else
      {
         selectRolePermissionQuery = selectUserPermissionQuery;
      }
   }
   
   public boolean grantPermission(Permission permission)
   {
      boolean recipientIsRole = permission.getRecipient() instanceof Role;
      
      try
      {
         if (recipientIsRole)
         {
            if (rolePermissionClass != null)
            {
               Object instance = rolePermissionClass.newInstance();
               roleTargetProperty.setValue(instance, permission.getTarget().toString());
               roleActionProperty.setValue(instance, permission.getAction());
               roleProperty.setValue(instance, permission.getRecipient().getName());
               lookupEntityManager().persist(instance);
               return true;
            }
            
            if (discriminatorProperty == null)
            {
               throw new RuntimeException("Could not grant permission, rolePermissionClass not set");   
            }
         }
         
         if (userPermissionClass == null)
         {
            throw new RuntimeException("Could not grant permission, userPermissionClass not set");
         }
                 
         Object instance = userPermissionClass.newInstance();
         targetProperty.setValue(instance, permission.getTarget().toString());
         actionProperty.setValue(instance, permission.getAction());
         userProperty.setValue(instance, permission.getRecipient().getName());
         
         if (discriminatorProperty != null)
         {
            discriminatorProperty.setValue(instance, getDiscriminatorValue(recipientIsRole));
         }
         
         lookupEntityManager().persist(instance);
         
         return true;
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Could not grant permission", ex);
      }   
   }
   
   private String getDiscriminatorValue(boolean isRole)
   {
      PermissionDiscriminator discriminator = (PermissionDiscriminator) discriminatorProperty.getAnnotation();
      return isRole ? discriminator.roleValue() : discriminator.userValue();      
   }
   
   public boolean revokePermission(Permission permission)
   {
      boolean recipientIsRole = permission.getRecipient() instanceof Role;

      EntityManager em = lookupEntityManager();
      
      Query qry = em.createQuery(recipientIsRole ? selectRolePermissionQuery :
         selectUserPermissionQuery)
         .setParameter("target", permission.getTarget())
         .setParameter("action", permission.getAction())
         .setParameter("recipient", resolvePrincipal(permission.getRecipient()));
      
      if (discriminatorProperty != null)
      {
         qry.setParameter("discriminator", getDiscriminatorValue(recipientIsRole));
      }
      
      try
      {
         Object instance = qry.getSingleResult();        
         em.remove(instance);
         return true;
      }
      catch (NoResultException ex)
      {
         return false;
      }
   }   
   
   /**
    * If the user or role properties in the entity class refer to other entities, then this method
    * uses JpaIdentityStore to lookup that user or role entity. 
    * 
    * @param recipient
    * @return
    */
   protected Object resolvePrincipal(Principal recipient)
   {
      boolean recipientIsRole = recipient instanceof Role;
         
      JpaIdentityStore identityStore = (JpaIdentityStore) Component.getInstance(JpaIdentityStore.class, true);
      
      if (identityStore != null)
      {
         if (recipientIsRole && roleProperty != null && roleProperty.getPropertyClass().equals(identityStore.getRoleClass()))
         {
            return identityStore.lookupRole(recipient.getName());
         }
         else if (userProperty.getPropertyClass().equals(identityStore.getUserClass()))
         {
            return identityStore.lookupUser(recipient.getName());
         }
      }      
      
      return recipient.getName();
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
   
   public List<Permission> listPermissions(Set<Object> targets)
   {
      // TODO implement this method
      return null;
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

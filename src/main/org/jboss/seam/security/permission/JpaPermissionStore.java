package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.jboss.seam.util.AnnotatedBeanProperty;

/**
 * A permission store implementation that uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.permission.jpaPermissionStore")
@Install(precedence = BUILT_IN, value=false) 
@Scope(APPLICATION)
@BypassInterceptors
public class JpaPermissionStore implements PermissionStore, Serializable
{
   private static final LogProvider log = Logging.getLogProvider(JpaPermissionStore.class);
   
   private enum Discrimination { user, role, either }
   
   private ValueExpression<EntityManager> entityManager;
   
   private Class userPermissionClass;
   private Class rolePermissionClass;
      
   private AnnotatedBeanProperty<PermissionUser> userProperty;
   private AnnotatedBeanProperty<PermissionRole> roleProperty;
   
   private AnnotatedBeanProperty<PermissionTarget> targetProperty;
   private AnnotatedBeanProperty<PermissionAction> actionProperty;   
   private AnnotatedBeanProperty<PermissionDiscriminator> discriminatorProperty;
   
   private AnnotatedBeanProperty<PermissionTarget> roleTargetProperty;
   private AnnotatedBeanProperty<PermissionAction> roleActionProperty;
   
   private Map<Integer,String> queryCache = new HashMap<Integer,String>();
   
   private IdentifierPolicy identifierPolicy;

   @Create
   public void init()
   {
      // TODO see if we can scan for this automatically      
      if (userPermissionClass == null)
      {
         log.debug("No permissionClass set, JpaDynamicPermissionStore will be unavailable.");
         return;
      }   
      
      if (entityManager == null)
      {
         entityManager = Expressions.instance().createValueExpression("#{entityManager}", 
               EntityManager.class);
      }       
      
      initProperties();
      
      identifierPolicy = (IdentifierPolicy) Component.getInstance(IdentifierPolicy.class, true);
   }   
   
   protected void initProperties()
   {
      userProperty = AnnotatedBeanProperty.scanForProperty(userPermissionClass, PermissionUser.class);
      targetProperty = AnnotatedBeanProperty.scanForProperty(userPermissionClass, PermissionTarget.class);
      actionProperty = AnnotatedBeanProperty.scanForProperty(userPermissionClass, PermissionAction.class);
      
      if (rolePermissionClass != null)
      {
         roleProperty = AnnotatedBeanProperty.scanForProperty(rolePermissionClass, PermissionRole.class);
         if (roleProperty != null)
         {
            roleTargetProperty = AnnotatedBeanProperty.scanForProperty(rolePermissionClass, 
                  PermissionTarget.class);
            roleActionProperty = AnnotatedBeanProperty.scanForProperty(rolePermissionClass, 
                  PermissionAction.class);
         }
      }
      else
      {
         roleProperty = AnnotatedBeanProperty.scanForProperty(userPermissionClass, PermissionRole.class);
         if (roleProperty != null)
         {
            discriminatorProperty = AnnotatedBeanProperty.scanForProperty(userPermissionClass, 
                  PermissionDiscriminator.class);
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
   
   protected Query createPermissionQuery(Object target, String action, Principal recipient, 
         Discrimination discrimination)
   {
      int queryKey = ((target != null) ? 1 : 0);
      queryKey |= (action != null ? 2 : 0);
      queryKey |= (recipient != null ? 4 : 0);
      queryKey |= (discrimination.equals(Discrimination.user) ? 8 : 0);
      queryKey |= (discrimination.equals(Discrimination.role) ? 16 : 0);
      queryKey |= (discrimination.equals(Discrimination.either) ? 32 : 0);
      
      boolean isRole = discrimination.equals(Discrimination.role) && rolePermissionClass != null;
      
      if (!queryCache.containsKey(queryKey))
      {  
         boolean conditionsAdded = false;
         
         StringBuilder q = new StringBuilder();
         q.append("select p from ");
         q.append(isRole ? rolePermissionClass.getName() : userPermissionClass.getName());
         q.append(" p");
         
         if (target != null)
         {
            q.append(" where ");
            q.append(isRole ? roleTargetProperty.getName() : targetProperty.getName());
            q.append(" = :target");
            conditionsAdded = true;
         }
         
         if (action != null)
         {
            q.append(conditionsAdded ? " and " : " where ");
            q.append(isRole ? roleActionProperty.getName() : actionProperty.getName());
            q.append(" = :action");
            conditionsAdded = true;
         }
         
         if (recipient != null)
         {
            q.append(conditionsAdded ? " and " : " where ");
            q.append(isRole ? roleProperty.getName() : userProperty.getName());
            q.append(" = :recipient");
            conditionsAdded = true;
         }
         
         // If there is no discrimination, then don't add such a condition to the query
         if (!discrimination.equals(Discrimination.either) && discriminatorProperty != null)
         {
            q.append(conditionsAdded ? " and " : " where ");
            q.append(discriminatorProperty.getName());
            q.append(" = :discriminator");
            conditionsAdded = true;
         }
         
         queryCache.put(queryKey, q.toString());
      }
      
      Query query = lookupEntityManager().createQuery(queryCache.get(queryKey));
      
      if (target != null) query.setParameter("target", identifierPolicy.getIdentifier(target));
      if (action != null) query.setParameter("action", action);
      if (recipient != null) query.setParameter("recipient", resolvePrincipal(recipient));
      
      if (!discrimination.equals(Discrimination.either) && discriminatorProperty != null) 
      {
         query.setParameter("discriminator", getDiscriminatorValue(
               discrimination.equals(Discrimination.role)));
      }
      
      return query;
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
               roleTargetProperty.setValue(instance, identifierPolicy.getIdentifier(permission.getTarget()));
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
         targetProperty.setValue(instance, identifierPolicy.getIdentifier(permission.getTarget()));
         actionProperty.setValue(instance, permission.getAction());         
         userProperty.setValue(instance, resolvePrincipal(permission.getRecipient()));
         
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
      PermissionDiscriminator discriminator = discriminatorProperty.getAnnotation();
      return isRole ? discriminator.roleValue() : discriminator.userValue();      
   }
   
   public boolean revokePermission(Permission permission)
   {
      Query qry = createPermissionQuery(permission.getTarget(), permission.getAction(), 
            permission.getRecipient(), permission.getRecipient() instanceof Role ? 
                  Discrimination.role : Discrimination.user);
            
      try
      {
         Object instance = qry.getSingleResult();        
         lookupEntityManager().remove(instance);
         return true;
      }
      catch (NoResultException ex)
      {
         return false;
      }
   }   
   
   /**
    * If the user or role properties in the entity class refer to other entities, then this method
    * uses the JpaIdentityStore (if available) to lookup that user or role entity.  Otherwise it
    * simply returns the name of the recipient. 
    * 
    * @param recipient
    * @return The entity or name representing the permission recipient
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
   
   protected String resolvePrincipalName(Object principal, boolean isUser
         )
   {
      if (principal instanceof String)
      {
         return (String) principal;
      }
      
      JpaIdentityStore identityStore = (JpaIdentityStore) Component.getInstance(JpaIdentityStore.class, true);
      
      if (identityStore != null)
      {
         if (isUser && identityStore.getUserClass().equals(principal.getClass()))
         {
            return identityStore.getUserName(principal);
         }
         
         if (!isUser && identityStore.getRoleClass().equals(principal.getClass()))
         {
            return identityStore.getRoleName(principal);
         }
      }
      
      throw new IllegalArgumentException("Cannot resolve principal name for principal " + principal); 
   }

   /**
    * Returns a list of all user and role permissions for a specific permission target and action.
    */
   public List<Permission> listPermissions(Object target, String action) 
   {
      List<Permission> permissions = new ArrayList<Permission>();
      
      // First query for user permissions
      Query permissionQuery = createPermissionQuery(target, action, null, Discrimination.either);
      List userPermissions = permissionQuery.getResultList(); 
      
      Map<String,Principal> principalCache = new HashMap<String,Principal>();
      
      boolean useDiscriminator = rolePermissionClass == null && discriminatorProperty != null;
      
      for (Object permission : userPermissions)
      {
         Principal principal;
         boolean isUser = true;
         
         if (useDiscriminator && 
            discriminatorProperty.getAnnotation().roleValue().equals(discriminatorProperty.getValue(permission)))
         {
            isUser = false;
         }

         String name = resolvePrincipalName(isUser ? userProperty.getValue(permission) :
            roleProperty.getValue(permission), isUser);
         
         String key = (isUser ? "u:" : "r:") + name;
         
         if (!principalCache.containsKey(key))
         {
            principal = isUser ? new SimplePrincipal(name) : new Role(name);
            principalCache.put(key, principal);
         }
         else
         {
            principal = principalCache.get(key);
         }
         
         permissions.add(new Permission(target, (String) (action != null ? action : actionProperty.getValue(permission)), 
               principal));
      }
      
      // If we have a separate class for role permissions, then query them now
      if (rolePermissionClass != null)
      {
         permissionQuery = createPermissionQuery(target, action, null, Discrimination.role);        
         List rolePermissions = permissionQuery.getResultList();
         
         for (Object permission : rolePermissions)
         {
            Principal principal;
            
            String name = resolvePrincipalName(roleProperty.getValue(permission), false);
            String key = "r:" + name;
            
            if (!principalCache.containsKey(key))
            {
               principal = new Role(name);
               principalCache.put(key, principal);
            }
            else
            {
               principal = principalCache.get(key);
            }
            
            permissions.add(new Permission(target, (String) (action != null ? action : 
               roleActionProperty.getValue(permission)), principal));
         }
      }
      
      return permissions;
   }

   public List<Permission> listPermissions(Object target) 
   {
      return listPermissions(target, null);
   }
   
   public List<String> listAvailableActions(Object target)
   {
      // TODO implement
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

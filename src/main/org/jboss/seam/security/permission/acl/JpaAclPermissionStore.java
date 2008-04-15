package org.jboss.seam.security.permission.acl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionStore;

/**
 * ACL permission storage, using JPA
 *  
 * @author Shane Bryzak
 */
public class JpaAclPermissionStore implements PermissionStore, Serializable
{
   private String entityManagerName = "entityManager";
   
   private Class permissionClass; 
   
   protected String getIdentifier(Object target)
   {
      return null;
   }
   
   public boolean grantPermission(Permission permission)
   {
      // TODO Auto-generated method stub
      return false;
   }

   public List<Permission> listPermissions(Object target)
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   public List<Permission> listPermissions(Set<Object> targets)
   {
      // TODO implement this
      return null;
   }
   
   public List<Permission> listPermissions(Object target, String action)
   {
      return null;
   }

   public boolean revokePermission(Permission permission)
   {
      // TODO Auto-generated method stub
      return false;
   }

   public String getEntityManagerName()
   {
      return entityManagerName;
   }
   
   public void setEntityManagerName(String entityManagerName)
   {
      this.entityManagerName = entityManagerName;
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

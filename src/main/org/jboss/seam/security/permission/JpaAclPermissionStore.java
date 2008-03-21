package org.jboss.seam.security.permission;

import java.io.Serializable;
import java.util.List;

public class JpaAclPermissionStore implements AclPermissionStore, Serializable
{
   private String entityManagerName = "entityManager";
   
   private Class<? extends AclPermission> permissionClass; 
   
   protected String getIdentifier(Object target)
   {
      return null;
   }
   
   public boolean grantPermission(Object target, String action, String account, AccountType accountType)
   {
      // TODO Auto-generated method stub
      return false;
   }

   public List<AclPermission> listPermissions(Object target)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean revokePermission(Object target, String action, String account, AccountType accountType)
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

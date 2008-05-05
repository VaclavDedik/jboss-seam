package org.jboss.seam.security.permission.action;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;

@Scope(CONVERSATION)
@Name("org.jboss.seam.security.permission.permissionSearch")
public class PermissionSearch implements Serializable
{
   @DataModel
   List<Permission> permissions;
   
   @DataModelSelection
   Permission selectedPermission;
   
   @In IdentityManager identityManager;
   
   @In PermissionManager permissionManager;
   
   private Object target;
   
   @Begin
   public void search(Object target)
   {
      this.target = target;      
   }
   
   public void refresh()
   {
      permissions = permissionManager.listPermissions(target);
   }
   
   public Object getTarget()
   {
      return target;
   }
   
   public Permission getSelectedPermission()
   {
      return selectedPermission;
   }
}

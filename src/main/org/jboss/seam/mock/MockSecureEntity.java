package org.jboss.seam.mock;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Permission;
import org.jboss.seam.annotations.security.Permissions;

/**
 * Used by security unit tests
 * 
 * @author Shane Bryzak
 */
@Name("mockSecureEntity")
@Install(false)
@Permissions( {
      @Permission(action = "read", expr = "#{aclPermissionChecker.checkPermission}"),
      @Permission(action = "delete", expr = "#{aclPermissionChecker.checkPermission}"),
      @Permission(action = "special", expr = "#{aclPermissionChecker.checkPermission}") })
@Entity
public class MockSecureEntity implements Serializable
{
   private static final long serialVersionUID = -6885685305122412324L;

   private Integer id;

   private String value;

   @Id
   public Integer getId()
   {
      return id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }
}

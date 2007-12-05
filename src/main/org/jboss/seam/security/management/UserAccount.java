package org.jboss.seam.security.management;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UserAccount implements Serializable
{
   private String username;
   private String passwordHash;
   private boolean enabled;
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
   }
   
   public String getPasswordHash()
   {
      return passwordHash;
   }
   
   public void setPasswordHash(String passwordHash)
   {
      this.passwordHash = passwordHash;
   }
   
   public boolean isEnabled()
   {
      return enabled;
   }
   
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }
}

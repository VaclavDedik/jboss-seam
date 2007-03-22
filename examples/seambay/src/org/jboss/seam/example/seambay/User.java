package org.jboss.seam.example.seambay;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User implements Serializable
{   
   private static final long serialVersionUID = 1L;
   
   private Integer userId;
   private String username;
   private String password;
   
   @Id
   public Integer getUserId()
   {
      return userId;
   }
   
   public void setUserId(Integer userId)
   {
      this.userId = userId;
   }
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
   }

   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      this.password = password;
   }
}

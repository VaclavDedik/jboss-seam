//$Id$
package org.jboss.seam.example.jbms;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Name("user")
@Scope(SESSION)
@Table(name="users")
public class User implements Serializable
{
   
   private String username;
   private String password;
   private String name;
   private String email;
   private boolean confirmed;
   
   @NotNull
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   @NotNull @Length(min=5, max=15)
   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }
   
   @Id @NotNull @Length(min=5, max=15)
   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }
   
   public String toString() 
   {
      return "User(" + username + ")";
   }
   
   @NotNull
   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public boolean isConfirmed()
   {
      return confirmed;
   }

   public void setConfirmed(boolean confirmed)
   {
      this.confirmed = confirmed;
   }
}

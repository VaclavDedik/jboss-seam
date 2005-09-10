//$Id$
package org.jboss.seam.example.registration;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Name("user")
@Scope(SESSION)
public class User implements Serializable
{
   private static final long serialVersionUID = 1881413500711441951L;
   
   private String username;
   private String password;
   private String name;
   
   public User(String name, String password, String username)
   {
      this.name = name;
      this.password = password;
      this.username = username;
   }
   
   public User() {}

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }
   
   @Id
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
}

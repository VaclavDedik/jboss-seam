//$Id: User.java,v 1.8 2007/06/27 00:06:49 gavin Exp $
package org.jboss.seam.example.booking;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static org.jboss.seam.ScopeType.SESSION;

@Entity
@Name("user")
@Scope(SESSION)
@Table(name = "Customer")
public class User implements Serializable {
   private String username;
   private String password;
   private String name;

   public User(String name, String password, String username) {
      this.name = name;
      this.password = password;
      this.username = username;
   }

   public User() {
   }

   @NotNull
   @Size(max = 100)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @NotNull
   @Size(min = 5, max = 15)
   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   @Id
   @Size(min = 4, max = 15)
   @Pattern(regexp = "^\\w*$", message = "not a valid username")
   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   @Override
   public String toString() {
      return "User(" + username + ")";
   }
}

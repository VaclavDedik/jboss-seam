//$Id: User.groovy 4698 2007-04-18 06:40:06Z ebernard $
package org.jboss.seam.example.groovy.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


import org.jboss.seam.ScopeType
import org.jboss.seam.annotations.Name
import org.jboss.seam.annotations.Scope
import javax.validation.constraints.Pattern
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Name("user")
@Scope(ScopeType.SESSION)
@Table(name="Customer")
class User implements Serializable
{
   @Id
   @Size(min=5, max=15)
   @Pattern(regexp=/^\w*$/, message="not a valid username")
   String username

   @NotNull
   @Size(min=5, max=15)
   String password

   @NotNull
   @Size(max=100)
   String name

   User(String name, String password, String username)
   {
      this.name = name
      this.password = password
      this.username = username
   }

   User() {}

   @Override
   String toString()
   {
      return "User(${username})"
   }
}

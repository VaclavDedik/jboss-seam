/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.example.bpm;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.AccessType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity( access= AccessType.FIELD )
@Table( name="T_USER" )
@Name( "user" )
@Scope( ScopeType.SESSION )
public class User implements Serializable
{
   @Id( generate = GeneratorType.AUTO )
   private Long id;
   private String name;
   private String username;
   private String password;

   public User()
   {
   }

   public User(String username, String password)
   {
      this.username = username;
      this.password = password;
   }

   public Long getId()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
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

   public void setId(Long id) {
      this.id = id;
   }

}

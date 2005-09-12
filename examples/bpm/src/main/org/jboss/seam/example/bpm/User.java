/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.example.bpm;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToMany;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Table( name="T_USER" )
@Name( "user" )
@Scope( ScopeType.CONVERSATION )
public class User implements Serializable
{
   private Long id;
   private String username;
   private String password;
   private State state = State.PENDING;
   private Set<Group> groups = new HashSet<Group>();

   public User()
   {
   }

   public User(String username, String password)
   {
      this.username = username;
      this.password = password;
   }

   @Id( generate = GeneratorType.AUTO )
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public State getState()
   {
      return state;
   }

   public void setState(State state)
   {
      this.state = state;
   }

   @ManyToMany()
   public Set<Group> getGroups()
   {
      return groups;
   }

   public void setGroups(Set<Group> groups)
   {
      this.groups = groups;
   }
}

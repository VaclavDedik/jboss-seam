/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.example.bpm;

import java.util.Set;
import java.util.HashSet;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratorType;
import javax.persistence.ManyToMany;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;

/**
 * Implementation of Group.
 *
 * @author Steve Ebersole
 */
@Entity
@Table( name="T_GROUP" )
@Name( "group" )
@Scope( ScopeType.CONVERSATION )
public class Group
{
   private Long id;
   private String name;
   private Set<User> users = new HashSet<User>();

   public Group()
   {
   }

   public Group(String name)
   {
      this.name = name;
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

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @ManyToMany
   public Set<User> getUsers()
   {
      return users;
   }

   public void setUsers(Set<User> users)
   {
      this.users = users;
   }
}

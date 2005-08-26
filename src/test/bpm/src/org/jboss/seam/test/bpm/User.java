/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.test.bpm;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Name( "user" )
@Scope( ScopeType.CONVERSATION )
public class User implements Serializable
{

   private int id;
   private String username;
   private String password;
   private State state = State.PENDING;

   public User()
   {
   }

   @Id( generate = GeneratorType.AUTO )
   public int getId()
   {
      return id;
   }

   void setId(int id)
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
}

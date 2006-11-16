package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Allows the application to specify the jBPM actorId
 * during the login cycle.
 * 
 * @author Gavin King
 */
@Name("org.jboss.seam.core.actor")
@Scope(ScopeType.SESSION)
@Intercept(NEVER)
public class Actor extends AbstractMutable implements Serializable
{
   
   private String id;
   private Set<String> groupActorIds = new HashSet<String>();
   
   //TODO: dirtyness for groupActorIds

   public String getId() 
   {
      return id;
   }

   public void setId(String id) 
   {
      setDirty(this.id, id);
      this.id = id;
   }
   
   public Set<String> getGroupActorIds()
   {
      return groupActorIds;
   }

   public static Actor instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (Actor) Component.getInstance(Actor.class, true);
   }
   
   @Override
   public String toString()
   {
      return "Actor(" + id + ")";
   }
   
}

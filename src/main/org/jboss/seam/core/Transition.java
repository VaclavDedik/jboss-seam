package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("transition")
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
public class Transition implements Serializable {
   private String name;

   public String getName() 
   {
      return name;
   }

   public void setName(String id) 
   {
      this.name = id;
   }
   
   public static Transition instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Transition) Component.getInstance(Transition.class, true);
   }
}

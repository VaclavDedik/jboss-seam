package org.jboss.seam.core;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.session")
@Intercept(InterceptionType.NEVER)
public class Session
{
   private boolean isInvalid;

   public boolean isInvalid()
   {
      return isInvalid;
   }

   public void invalidate()
   {
      this.isInvalid = true;
   }

   public static Session instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("No active event context");
      }
      return (Session) Component.getInstance(Conversation.class, ScopeType.EVENT);
   }
   
}

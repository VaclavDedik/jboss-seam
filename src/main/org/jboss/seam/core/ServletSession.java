package org.jboss.seam.core;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.servletSession")
@Intercept(InterceptionType.NEVER)
public class ServletSession
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

   public static ServletSession instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("No active event context");
      }
      return (ServletSession) Component.getInstance(ServletSession.class, ScopeType.EVENT);
   }
   
}

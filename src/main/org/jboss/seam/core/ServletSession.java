package org.jboss.seam.core;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@Scope(ScopeType.SESSION)
@Name("org.jboss.seam.core.servletSession")
@BypassInterceptors
@Startup
public class ServletSession extends AbstractMutable
{
   private boolean isInvalid;
   private boolean invalidateOnSchemeChange;
   private String currentScheme;

   public boolean isInvalid()
   {
      return isInvalid;
   }

   public void invalidate()
   {
      this.isInvalid = true;
      setDirty();
   }
   
   public boolean isInvalidDueToNewScheme(String requestScheme)
   {
      if (invalidateOnSchemeChange)
      {
         if ( currentScheme==null )
         {
            currentScheme = requestScheme;
            setDirty();
            return false;
         }
         else if ( !currentScheme.equals(requestScheme) )
         {
            currentScheme = requestScheme;
            setDirty();
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   public boolean isInvalidateOnSchemeChange()
   {
      return invalidateOnSchemeChange;
   }

   public void setInvalidateOnSchemeChange(boolean invalidateOnSchemeChange)
   {
      setDirty();
      this.invalidateOnSchemeChange = invalidateOnSchemeChange;
   }
   
   public static ServletSession instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (ServletSession) Component.getInstance(ServletSession.class, ScopeType.SESSION);
   }

   public static ServletSession getInstance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (ServletSession) Component.getInstance(ServletSession.class, ScopeType.SESSION, false);
   }

}

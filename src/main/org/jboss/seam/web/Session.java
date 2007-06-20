package org.jboss.seam.web;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.AbstractMutable;

@Scope(ScopeType.SESSION)
@Name("org.jboss.seam.web.session")
@BypassInterceptors
@Startup
public class Session extends AbstractMutable
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
   
   public static Session instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (Session) Component.getInstance(Session.class, ScopeType.SESSION);
   }

   public static Session getInstance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (Session) Component.getInstance(Session.class, ScopeType.SESSION, false);
   }

}

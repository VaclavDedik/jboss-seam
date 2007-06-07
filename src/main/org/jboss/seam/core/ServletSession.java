package org.jboss.seam.core;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Scope(ScopeType.SESSION)
@Name("org.jboss.seam.core.servletSession")
@Intercept(InterceptionType.NEVER)
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
   
   public boolean isInvalidDueToNewScheme()
   {
      if (invalidateOnSchemeChange)
      {
         FacesContext facesContext = FacesContext.getCurrentInstance();
         String requestScheme = Pages.getRequestScheme(facesContext);
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
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("No active event context");
      }
      return (ServletSession) Component.getInstance(ServletSession.class, ScopeType.SESSION);
   }

}

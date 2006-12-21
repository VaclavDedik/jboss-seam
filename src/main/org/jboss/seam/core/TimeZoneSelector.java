package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Selects the current user's time zone, defaulting
 * to the server time zone.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.SESSION)
@Name("org.jboss.seam.core.timeZoneSelector")
@Intercept(NEVER)
@Install(value=false, precedence=BUILT_IN)
public class TimeZoneSelector extends Selector
{
   private static final long serialVersionUID = -5013819375360015369L;
   
   private String id;
   
   @Create
   public void initTimeZone()
   {
      String timeZoneId = getCookieValue();
      if (timeZoneId!=null) setTimeZoneId(timeZoneId);
   }
   
   @Override
   protected String getCookieName()
   {
      return "org.jboss.seam.core.TimeZone";
   }
   
   /**
    * Force the resource bundle to reload, using the current locale, 
    * and raise the org.jboss.seam.timeToneSelected event
    */
   public void select()
   {
      setCookieValue( getTimeZoneId() );

      if ( Events.exists() ) 
      {
          Events.instance().raiseEvent( "org.jboss.seam.timeToneSelected", getTimeZoneId() );
      }
   }

   public void setTimeZone(java.util.TimeZone timeZone)
   {
      setTimeZoneId( timeZone.getID() );
   }

   public void setTimeZoneId(String id)
   {
      setDirty(this.id, id);
      this.id = id;
   }
   
   public String getTimeZoneId()
   {
      return id;
   }

   /**
    * Get the selected timezone
    */
   public java.util.TimeZone getTimeZone() 
   {
      if (id==null)
      {
         return java.util.TimeZone.getDefault();
      }
      else
      {
         return java.util.TimeZone.getTimeZone( getTimeZoneId() );
      }
   }

   public static TimeZoneSelector instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }
      return (TimeZoneSelector) Component.getInstance(TimeZoneSelector.class, ScopeType.SESSION);
   }
   
}

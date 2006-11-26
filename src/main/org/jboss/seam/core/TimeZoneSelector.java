package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
public class TimeZoneSelector extends AbstractMutable implements Serializable
{

   private String id;
   
   private boolean cookieEnabled;
   private int cookieMaxAge = 31536000; //1 year
   
   @Create
   public void initTimeZone()
   {
      if (cookieEnabled)
      {
         Cookie cookie = (Cookie) FacesContext.getCurrentInstance().getExternalContext()
               .getRequestCookieMap().get("org.jboss.seam.core.TimeZone");
         if (cookie!=null) setTimeZoneId( cookie.getValue() );
      }
   }
   
   /**
    * Force the resource bundle to reload, using the current locale
    */
   public void select()
   {
      if (cookieEnabled)
      {
         HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
         Cookie cookie = new Cookie( "org.jboss.seam.core.TimeZone", getTimeZoneId() );
         cookie.setMaxAge(cookieMaxAge);
         response.addCookie(cookie);
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

   public boolean isCookieEnabled()
   {
      return cookieEnabled;
   }

   public void setCookieEnabled(boolean cookieEnabled)
   {
      setDirty(this.cookieEnabled, cookieEnabled);
      this.cookieEnabled = cookieEnabled;
   }

   protected int getCookieMaxAge()
   {
      return cookieMaxAge;
   }

   protected void setCookieMaxAge(int cookieMaxAge)
   {
      this.cookieMaxAge = cookieMaxAge;
   }
   
}

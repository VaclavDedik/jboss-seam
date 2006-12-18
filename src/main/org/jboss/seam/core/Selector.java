package org.jboss.seam.core;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Support for selector objects which remember their selection as a cookie
 * 
 * @author Gavin King
 */
public abstract class Selector extends AbstractMutable implements Serializable
{
   private boolean cookieEnabled;
   private int cookieMaxAge = 31536000; //1 year
   
   /**
    * Is the cookie enabled?
    * @return false by default
    */
   public boolean isCookieEnabled()
   {
      return cookieEnabled;
   }

   public void setCookieEnabled(boolean cookieEnabled)
   {
      setDirty(this.cookieEnabled, cookieEnabled);
      this.cookieEnabled = cookieEnabled;
   }

   /**
    * The max age of the cookie
    * @return 1 year by default
    */
   public int getCookieMaxAge()
   {
      return cookieMaxAge;
   }

   public void setCookieMaxAge(int cookieMaxAge)
   {
      this.cookieMaxAge = cookieMaxAge;
   }
   
   /**
    * Override to define the cookie name
    */
   protected abstract String getCookieName();
   
   /**
    * Get the value of the cookie
    */
   protected String getCookieValue()
   {
      if ( isCookieEnabled() )
      {
         Cookie cookie = (Cookie) FacesContext.getCurrentInstance().getExternalContext()
               .getRequestCookieMap().get( getCookieName() );
         return cookie==null ? null : cookie.getValue();
      }
      else
      {
         return null;
      }
   }
   
   /**
    * Set the cookie
    */
   protected void setCookieValue(String value)
   {
      if ( isCookieEnabled() )
      {
         HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
         Cookie cookie = new Cookie( getCookieName(), value );
         cookie.setMaxAge( getCookieMaxAge() );
         response.addCookie(cookie);
      }
   }
}

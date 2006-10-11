package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Manager component for the current user's locale
 * 
 * @author Gavin King
 */
@Scope(ScopeType.STATELESS)
@Name("timeZone")
@Intercept(NEVER)
public class TimeZone {

   @Unwrap
   public java.util.TimeZone getLocale()
   {
      return TimeZoneSelector.instance().getTimeZone();
   }
   
   public static java.util.Locale instance()
   {
      return (java.util.Locale) Component.getInstance(TimeZone.class, ScopeType.STATELESS, true);
   }
   
}

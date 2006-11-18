package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
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
@Name("org.jboss.seam.core.timeZone")
@Intercept(NEVER)
@Install(false)
public class TimeZone {

   @Unwrap
   public java.util.TimeZone getTimeZone()
   {
      return TimeZoneSelector.instance().getTimeZone();
   }
   
   public static java.util.TimeZone instance()
   {
      return (java.util.TimeZone) Component.getInstance(TimeZone.class, ScopeType.STATELESS);
   }
   
}

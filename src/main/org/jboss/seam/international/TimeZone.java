package org.jboss.seam.international;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

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
@Name("org.jboss.seam.international.timeZone")
@Intercept(NEVER)
@Install(precedence=BUILT_IN, dependencies="org.jboss.seam.international.timeZoneSelector")
public class TimeZone 
{

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

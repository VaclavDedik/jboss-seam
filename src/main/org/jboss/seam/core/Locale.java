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
@Name("locale")
@Intercept(NEVER)
public class Locale {

   @Unwrap
   public java.util.Locale getLocale()
   {
      return LocaleSelector.instance().getLocale();
   }
   
   public static java.util.Locale instance()
   {
      return (java.util.Locale) Component.getInstance(Locale.class, ScopeType.STATELESS, true);
   }
   
}

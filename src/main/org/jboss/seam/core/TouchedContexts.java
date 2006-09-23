package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Mutable;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Name("touchedContexts")
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
@Mutable
public class TouchedContexts
{
   private Set<String> set = new HashSet<String>();
   
   @Unwrap
   public Set<String> getTouchedContexts()
   {
      return set;
   }
   
   public static Set<String> instance()
   {
      return (Set<String>) Component.getInstance(TouchedContexts.class);
   }
   
}

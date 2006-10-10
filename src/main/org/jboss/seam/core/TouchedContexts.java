package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("org.jboss.seam.core.touchedContexts")
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
public class TouchedContexts extends AbstractMutable implements Serializable
{
   private Set<String> set = new HashSet<String>();
   
   public Set<String> getTouchedContexts()
   {
      return Collections.unmodifiableSet(set);
   }
   
   public void touch(String context)
   {
      if ( set.add(context) ) setDirty();
   }
   
   public static TouchedContexts instance()
   {
      if ( Contexts.isConversationContextActive() )
      {
         return (TouchedContexts) Component.getInstance(TouchedContexts.class);
      }
      else
      {
         return null;
      }
   }
   
}

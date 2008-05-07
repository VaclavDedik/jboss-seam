package org.jboss.seam.ui.resource;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@Scope(APPLICATION)
@Name("org.jboss.seam.ui.resource.safeStyleResources")
@BypassInterceptors
@Install(precedence = BUILT_IN)
public class SafeStyleResources
{

   private Set<String> safeStyleResources = new HashSet<String>();
   
   public void addSafeStyleResource(String path)
   {
      this.safeStyleResources.add(path);
   }
   
   public boolean isStyleResourceSafe(String path)
   {
      if (safeStyleResources.contains(path))
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   public static SafeStyleResources instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (SafeStyleResources) (Component.getInstance(SafeStyleResources.class));
   }
   
}

package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;

/**
 * Manager component for a map of roles assigned
 * to the current user, as exposed via the JSF
 * ExternalContext.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("isUserInRole")
public class IsUserInRole
{
   @Unwrap
   public Map<String, Boolean> getMap()
   {
      return new AbstractMap<String, Boolean>()
      {
         @Override
         public Set<Map.Entry<String, Boolean>> entrySet() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Boolean get(Object key)
         {
            if ( !(key instanceof String ) ) return false;
            return FacesContext.getCurrentInstance().getExternalContext().isUserInRole( (String) key );
         }
         
      };
   }
}

package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Lifecycle;

/**
 * Manager component for a map of roles assigned
 * to the current user, as exposed via the JSF
 * ExternalContext.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.isUserInRole")
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
            
            String role = (String) key;
            
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if ( facesContext != null ) 
            {
               return facesContext.getExternalContext().isUserInRole(role);
            }
            
            ServletRequest servletRequest = Lifecycle.getServletRequest();
            if ( servletRequest != null )
            {
               return ( (HttpServletRequest) servletRequest ).isUserInRole(role);
            }
            
            return null;
         }
         
      };
   }
}

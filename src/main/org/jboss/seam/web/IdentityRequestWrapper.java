package org.jboss.seam.web;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.jboss.seam.Seam;
import org.jboss.seam.security.Identity;

/**
 * An HttpServletRequestWrapper implementation that provides integration
 * between Servlet Security and the Seam identity component.
 *
 * @author Dan Allen
 */
class IdentityRequestWrapper extends HttpServletRequestWrapper {

   private Identity identity;

   public IdentityRequestWrapper(HttpServletRequest request) {
      super(request);
      identity = (Identity) request.getSession().
         getAttribute(Seam.getComponentName(Identity.class));
   }

   @Override
   public String getRemoteUser() {
      return getUserPrincipal() != null ? getUserPrincipal().getName() : null;
   }

   @Override
   public Principal getUserPrincipal() {
      return Identity.isSecurityEnabled() ? identity.getPrincipal() : null;
   }

   @Override
   public boolean isUserInRole(String role) {
      return getUserPrincipal() != null ? identity.hasRole(role) : false;
   }
}

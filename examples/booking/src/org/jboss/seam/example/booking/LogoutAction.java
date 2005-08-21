//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@LoggedIn
@Name("logout")
@LocalBinding(jndiBinding="logout")
@Interceptor(SeamInterceptor.class)
public class LogoutAction implements Logout
{
   public String logout()
   {
      Contexts.invalidateSession();
      return "login";
   }
}

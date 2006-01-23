//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.STATELESS;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.example.hibernate.LoggedIn;

@LoggedIn
@Name("logout")
@Scope(STATELESS)
public class LogoutAction
{
   public String logout()
   {
      Seam.invalidateSession();
      return "login";
   }
}

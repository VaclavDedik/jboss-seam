//$Id$
package org.jboss.seam.example.noejb;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;

@LoggedIn
@Name("logout")
public class LogoutAction
{
   public String logout()
   {
      Seam.invalidateSession();
      return "login";
   }
}

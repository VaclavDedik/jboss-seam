//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Stateless;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;

@Stateless
@LoggedIn
@Name("logout")
public class LogoutAction implements Logout
{
   public String logout()
   {
      Seam.invalidateSession();
      return "login";
   }
}

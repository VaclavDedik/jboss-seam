//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface Login
{
   public void login();
   public void logout();
   public void validateLogin();
   public boolean isLoggedIn();
   
   public void destroy();
}

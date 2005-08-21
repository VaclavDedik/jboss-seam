//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface ChangePassword
{
   public String changePassword();
   public String getVerify();
   public void setVerify(String verify);
   
   public void destroy();
}
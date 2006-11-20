//$Id: ChangePassword.java,v 1.2 2005/09/10 16:23:35 gavin Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface ChangePassword
{
   public String changePassword();
   public String cancel();
   
   public String getVerify();
   public void setVerify(String verify);
   
   public void destroy();
}
//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface Register
{
   public String register();
   public String getVerify();
   public void setVerify(String verify);
   
   public void destroy();
}
//$Id: Register.java,v 1.1 2005/08/23 09:17:39 gavin Exp $
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
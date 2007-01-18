package org.jboss.seam.example.seamspace;

import javax.ejb.Local;

@Local
public interface Register
{
   void start();
   void next();
   String getConfirm();
   void setConfirm(String confirm);
   String getGender();
   void setGender(String gender);
   void destroy();
}

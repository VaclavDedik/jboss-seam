package org.jboss.seam.example.seamspace;

import javax.ejb.Local;

import org.jboss.seam.util.UploadedFile;

@Local
public interface Register
{
   void start();
   void next();
   void uploadPicture();
   String getConfirm();
   void setConfirm(String confirm);
   String getGender();
   void setGender(String gender);
   byte[] getPicture();
   void setPicture(byte[] picture);
   void destroy();
}

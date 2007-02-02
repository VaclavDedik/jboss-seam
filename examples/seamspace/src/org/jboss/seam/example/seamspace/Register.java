package org.jboss.seam.example.seamspace;

import javax.ejb.Local;
import javax.security.auth.login.LoginException;

@Local
public interface Register
{
   void start();
   void next();
   void uploadPicture() throws LoginException;
   String getConfirm();
   void setConfirm(String confirm);
   String getGender();
   void setGender(String gender);
   
   byte[] getPicture();
   void setPicture(byte[] picture);
   
   String getPictureContentType();
   void setPictureContentType(String contentType);
   
   boolean isVerified();
   
   String getCaptchaId();
   String getVerifyCaptcha();
   void setVerifyCaptcha(String verifyCaptcha);
   
   void destroy();
}

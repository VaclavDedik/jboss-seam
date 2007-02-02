package org.jboss.seam.example.seamspace;

import java.rmi.server.UID;
import java.util.Date;
import java.util.HashSet;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.CaptchaService;
import org.jboss.seam.security.Identity;

@Stateful
@Name("register")
public class RegisterAction implements Register
{
   @In(required = false) @Out
   private Member newMember;
   
   @In
   private EntityManager entityManager;
   
   @In
   private Identity identity;
      
   /**
    * Password confirmation
    */
   private String password;
   private String confirm;
   
   
   private String gender;
   
   private byte[] picture;
   private String pictureContentType;
   
   private boolean verified;

   private String captchaId;
   private String verifyCaptcha;   

   @Factory("newMember") @Begin
   public void start()
   {
      newMember = new Member();
      captchaId = new UID().toString().replace(":", "-");
   }
   
   public void next()
   {
      newMember.setGender(Member.Gender.valueOf(gender.toLowerCase()));
      
      verified = (confirm != null && confirm.equals(password));
      
      if (!verified)
      {
         FacesMessages.instance().add("confirmPassword", "Passwords do not match");
      }
            
      newMember.setHashedPassword(Hash.instance().hash(password));
            
      try
      {
         if (!CaptchaService.instance().getService().validateResponseForID(
               getCaptchaId(), verifyCaptcha))
         {
            FacesMessages.instance().add("verifyCaptcha", "Verification incorrect");
            verified = false;            
         }
      }
      catch (Exception ex)
      {
         verified = false;
      }
   }

   @End
   public void uploadPicture() 
   {
      newMember.setMemberSince(new Date());
      newMember.setRoles(new HashSet<MemberRole>());
      
      MemberRole userRole = (MemberRole) entityManager.createQuery(
            "from MemberRole where name = 'user'")
            .getSingleResult();
      
      newMember.getRoles().add(userRole);

      entityManager.persist(newMember);

      if (picture != null)
      {
         MemberImage img = new MemberImage();
         img.setData(picture);
         img.setMember(newMember);
         img.setContentType(pictureContentType);
         entityManager.persist(img);
         newMember.setPicture(img);
         
         newMember = entityManager.merge(newMember);
      }
      
      // Login the user
      identity.setUsername(newMember.getUsername());
      identity.setPassword(password);
      identity.login();
   }
   
   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      this.password = password;
   }
   
   public String getConfirm()
   {
      return confirm;
   }
   
   public void setConfirm(String confirm)
   {
      this.confirm = confirm;
   }
   
   public String getGender()
   {
      return gender;
   }
   
   public void setGender(String gender)
   {
      this.gender = gender;
   }
   
   public void setPicture(byte[] picture)
   {
      this.picture = picture;
   }
   
   public byte[] getPicture()
   {
      return picture;
   }
   
   public String getPictureContentType()
   {
      return pictureContentType;  
   }
   
   public void setPictureContentType(String contentType)
   {
      this.pictureContentType = contentType;
   }
   
   public boolean isVerified()
   {
      return verified;
   }
   
   public String getCaptchaId()
   {
      return captchaId;
   }
   
   public String getVerifyCaptcha()
   {
      return verifyCaptcha;
   }
   
   public void setVerifyCaptcha(String verifyCaptcha)
   {
      this.verifyCaptcha = verifyCaptcha;
   }
      
   @Destroy @Remove
   public void destroy() {}
}

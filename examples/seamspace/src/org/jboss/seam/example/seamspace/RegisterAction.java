package org.jboss.seam.example.seamspace;

import java.util.Date;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.FacesMessages;

@Stateful
@Name("register")
public class RegisterAction implements Register
{
   @In(required = false) @Out
   private Member newMember;
   
   @In(create = true)
   private EntityManager entityManager;
   
   /**
    * Password confirmation
    */
   private String confirm;
   
   private String gender;
   
   private byte[] picture;

   @Factory("newMember") @Begin
   public void start()
   {
      newMember = new Member();
   }
   
   public void next()
   {
      if (confirm == null || !confirm.equals(newMember.getPassword()))
         FacesMessages.instance().add("confirmPassword", "Passwords do not match");
      
      newMember.setGender(Member.Gender.valueOf(gender.toLowerCase()));
   }
   
   public void uploadPicture() 
   {
      MemberImage img = new MemberImage();
      img.setData(picture);
      img.setMember(newMember);
      newMember.setPicture(img);
      newMember.setMemberSince(new Date());
      
      entityManager.persist(img);
      entityManager.persist(newMember);
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
   
   @Destroy @Remove
   public void destroy() {}
}

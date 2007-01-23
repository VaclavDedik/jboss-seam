package org.jboss.seam.example.seamspace;

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

@Stateful
@Name("register")
public class RegisterAction implements Register
{
   @In(required = false) @Out
   private Member newMember;
   
   @In(create = true)
   private EntityManager entityManager;
   
   @In(create = true)
   private LoginLocal login;
   
   @In(required = false)
   Member member;   
   
   /**
    * Password confirmation
    */
   private String confirm;
   
   private String gender;
   
   private byte[] picture;
   private String pictureContentType;

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
      member.setUsername(newMember.getUsername());
      member.setPassword(newMember.getPassword());
      login.login();
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
   
   @Destroy @Remove
   public void destroy() {}
}

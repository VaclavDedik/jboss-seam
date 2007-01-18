package org.jboss.seam.example.seamspace;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
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
   
   @Destroy @Remove
   public void destroy() {}
}

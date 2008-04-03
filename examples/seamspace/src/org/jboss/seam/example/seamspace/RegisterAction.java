package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Date;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;

@Scope(CONVERSATION)
@Name("register")
public class RegisterAction
{
   @In(required = false) @Out
   private Member newMember;
   
   @In
   private EntityManager entityManager;
   
   @In
   private Identity identity;
   
   @In
   private IdentityManager identityManager;
      
   private MemberAccount newAccount;
   
   private String username;   
   
   /**
    * Password confirmation
    */
   private String password;
   private String confirm;   
   
   private String gender;
   
   private byte[] picture;
   private String pictureContentType;
   private String pictureFileName;
   
   private boolean verified;

   @Factory("newMember") @Begin(join = true)
   public void start()
   {
      newMember = new Member();
   }
   
   public void next()
   {
      newMember.setGender(Member.Gender.valueOf(gender.toLowerCase()));
      
      verified = (confirm != null && confirm.equals(password));
      
      if (!verified)
      {
         FacesMessages.instance().addToControl("confirmPassword", "Passwords do not match");
      }           
   }
   
   @Observer(JpaIdentityStore.EVENT_USER_CREATED)
   public void accountCreated(MemberAccount account)
   {
      // The user *may* have been created from the user manager screen. In that
      // case, create a dummy Member record just for the purpose of demonstrating the
      // identity management API
      if (newMember == null)
      {
         newMember = new Member();
         newMember.setMemberName(account.getUsername());
         newMember.setGender(Member.Gender.male);
         newMember.setFirstName("John");
         newMember.setLastName("Doe");
         newMember.setEmail(account.getUsername() + "@nowhere.com");
         newMember.setDob(new Date());
         newMember.setMemberSince(new Date());
         entityManager.persist(newMember);
      }
      
      account.setMember(newMember);
      this.newAccount = account;
   }

   @End
   public void uploadPicture() 
   {  
      newMember.setMemberSince(new Date());      
      entityManager.persist(newMember);      
      
      new RunAsOperation() {
         @Override
         public String[] getRoles() {
            return new String[] { "admin" };
         }
         
         public void execute() {
            identityManager.createUser(username, password);
            identityManager.grantRole(username, "user");            
         }         
      }.run();
            
      newAccount.setMember(newMember);
      newAccount = entityManager.merge(newAccount);

      if (picture != null && picture.length > 0)
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
      identity.setUsername(username);
      identity.setPassword(password);
      identity.login();
   }
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
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
   
   public String getPictureFileName()
   {
      return pictureFileName;
   }
   
   public void setPictureFileName(String filename)
   {
      this.pictureFileName = filename;
   }
   
   public boolean isVerified()
   {
      return verified;
   }
   
   @Destroy @Remove
   public void destroy() {}
}

package org.jboss.seam.example.registration;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.example.util.MessageUtil;

@Stateless
@Name("register")
public class RegisterAction implements Register {

   @In @Valid
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @IfInvalid(outcome=Outcome.REDISPLAY)
   public String register() {
      if (validNewUser()){
         em.persist(user);
         return "success";
      }
      else {
         MessageUtil.addMsg("msg.already.exists");
         return null;
      }
   }    
   
   private boolean validNewUser() {
	   List existing = em.createQuery("select username from User where username=:username")
                         .setParameter("username", user.getUsername())
                         .getResultList();
	   if (existing.size()==0) {
		   return true;
	   }
	   else {
		   return false;
	   }
   }
}

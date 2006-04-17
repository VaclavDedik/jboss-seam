//$Id$
package org.jboss.seam.example.registration;

import java.util.List;

import javax.ejb.Interceptors;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("register")
public class RegisterAction implements Register
{

   @In @Valid
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @IfInvalid(outcome=Outcome.REDISPLAY)
   public String register()
   {
      List existing = em.createQuery("select username from User where username=:username")
         .setParameter("username", user.getUsername())
         .getResultList();
      if (existing.size()==0)
      {
         em.persist(user);
         return "success";
      }
      else
      {
         FacesMessages.instance().add("User #{user.username} already exists");
         return null;
      }
   }

}

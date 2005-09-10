//$Id$
package org.jboss.seam.example.registration;

import static org.jboss.seam.ScopeType.EVENT;

import java.util.List;

import javax.ejb.Interceptor;
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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Scope(EVENT)
@Name("register")
@Interceptor(SeamInterceptor.class)
public class RegisterAction implements Register
{

   @In @Valid
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @In
   private FacesContext facesContext;
   
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
         facesContext.addMessage(null, new FacesMessage("username already exists"));
         return null;
      }
   }

}

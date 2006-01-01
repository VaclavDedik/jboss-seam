//$Id$
package org.jboss.seam.example.booking;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.util.List;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Scope(EVENT)
@Name("register")
@Interceptors(SeamInterceptor.class)
public class RegisterAction implements Register
{
   
   private static final Logger log = Logger.getLogger(Register.class);

   @In @Valid
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @In
   private FacesContext facesContext;
   
   private String verify;
   
   @IfInvalid(outcome=REDISPLAY)
   public String register()
   {
      if ( user.getPassword().equals(verify) )
      {
         log.info("registering user");
         List existing = em.createQuery("select username from User where username=:username")
            .setParameter("username", user.getUsername())
            .getResultList();
         if (existing.size()==0)
         {
            em.persist(user);
            return "login";
         }
         else
         {
            facesContext.addMessage(null, new FacesMessage("username already exists"));
            return null;
         }
      }
      else 
      {
         log.info("password not verified");
         facesContext.addMessage(null, new FacesMessage("re-enter your password"));
         verify=null;
         return null;
      }
   }

   public String getVerify()
   {
      return verify;
   }

   public void setVerify(String verify)
   {
      this.verify = verify;
   }
   
   @Destroy @Remove
   public void destroy()
   {
      log.info("destroyed");
   }
}

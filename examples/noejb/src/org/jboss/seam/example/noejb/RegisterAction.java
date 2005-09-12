//$Id$
package org.jboss.seam.example.noejb;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.util.List;

import javax.faces.application.FacesMessage;

import org.hibernate.Session;
import org.hibernate.validator.Valid;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(EVENT)
@Name("register")
public class RegisterAction
{
   
   private static final Logger log = Logger.getLogger(RegisterAction.class);

   @In @Valid
   private User user;
   
   @In(create=true)
   private Session bookingDatabase;
   
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
            bookingDatabase.persist(user);
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
   
}

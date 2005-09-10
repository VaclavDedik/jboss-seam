//$Id$
package org.jboss.seam.example.booking;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import javax.ejb.Interceptor;
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
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Scope(EVENT)
@Name("changePassword")
@Interceptor(SeamInterceptor.class)
@LoggedIn
public class ChangePasswordAction implements ChangePassword
{
   
   private static final Logger log = Logger.getLogger(ChangePassword.class);

   @In @Out @Valid
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @In
   private FacesContext facesContext;
   
   private String verify;
   
   @IfInvalid(outcome=REDISPLAY)
   public String changePassword()
   {
      if ( user.getPassword().equals(verify) )
      {
         log.info("updating password to: " + verify);
         user = em.merge(user);
         return "main";
      }
      else 
      {
         log.info("password not verified");
         facesContext.addMessage(null, new FacesMessage("Re-enter new password"));
         revertUser();
         verify=null;
         return null;
      }
   }
   
   public String cancel()
   {
      revertUser();
      return "main";
   }

   private void revertUser()
   {
      user = em.find(User.class, user.getUsername());
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

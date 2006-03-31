//$Id$
package org.jboss.seam.example.booking;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.util.List;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Scope(EVENT)
@Name("register")
@Interceptors(SeamInterceptor.class)
public class RegisterAction implements Register
{

   @In @Valid
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @In(create=true)
   private transient FacesMessages facesMessages;
   
   private String verify;
   
   @IfInvalid(outcome=REDISPLAY)
   public String register()
   {
      if ( user.getPassword().equals(verify) )
      {
         List existing = em.createQuery("select username from User where username=:username")
            .setParameter("username", user.getUsername())
            .getResultList();
         if (existing.size()==0)
         {
            em.persist(user);
            facesMessages.add("Successfully registered as #{user.username}");
            return "login";
         }
         else
         {
            facesMessages.add("Username #{user.username} already exists");
            return null;
         }
      }
      else 
      {
         facesMessages.add("Re-enter your password");
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
   public void destroy() {}
}

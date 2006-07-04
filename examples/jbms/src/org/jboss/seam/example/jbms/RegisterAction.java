//$Id$
package org.jboss.seam.example.jbms;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.Depends;
import org.jboss.mail.userapi.MailSender;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.core.FacesMessages;

@Stateless
@Name("register")
public class RegisterAction implements Register
{
   @Depends("jboss.mail:type=MailServices,name=MailSender")
   private MailSender mailer;

   @In
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
         String from = user.getEmail();  //todo this should be a sender acct
         String to = from;
         mailer.send(user.getUsername(), from, new String[]{to}, 
                     new String[]{},new String[]{},"Hi user","This is a mail to a user\r\n\r\nHi user\r\n.\r\n");
         return "success";
      }
      else
      {
         FacesMessages.instance().add("User #{user.username} already exists");
         return null;
      }
   }

}

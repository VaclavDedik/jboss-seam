//$Id$
package org.jboss.seam.example.registration;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletRequest;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.log.Log;

@Stateless
@Name("register")
public class RegisterAction implements Register
{

   @In
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @Logger
   private Log log;
   
   /**
    * This demonstrates passing a parameter to an action from 
    * pages.xml.  In this case, we call this method using
    * <page view-id="/registered.jsp" 
    *       action="#{register.logClientIP(facesContext)}" /> 
    *
    * Note that facesContext is a reserved word that always
    * binds to the current FacesContext instance.
    */
   public void logClientIP(FacesContext facesContext)
   {
      ExternalContext extCtx = facesContext.getExternalContext();
      if (extCtx.getRequest() instanceof ServletRequest)
      {
         ServletRequest request = (ServletRequest)extCtx.getRequest();
         log.info("Registered user from IP: " + request.getRemoteAddr());
      }
   }

   public String register()
   {
      List existing = em.createQuery("select username from User where username=:username")
         .setParameter("username", user.getUsername())
         .getResultList();
      if (existing.size()==0)
      {
         em.persist(user);
         log.info("Registered new user #{user.username}");
         return "/registered.jsp";
      }
      else
      {
         FacesMessages.instance().add("User #{user.username} already exists");
         return null;
      }
   }

}

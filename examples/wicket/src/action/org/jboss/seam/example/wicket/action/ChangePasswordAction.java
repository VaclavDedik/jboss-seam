//$Id: ChangePasswordAction.java,v 1.22 2007/06/27 00:06:49 gavin Exp $
package org.jboss.seam.example.wicket.action;

import static org.jboss.seam.ScopeType.EVENT;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;

@Stateful
@Scope(EVENT)
@Name("changePassword")
@Restrict("#{identity.loggedIn}")
public class ChangePasswordAction implements ChangePassword
{
   @In @Out
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   //@In
   //private FacesMessages facesMessages;
   
   public void changePassword()
   {
      user = em.merge(user);
      //facesMessages.add("Password updated");
   }
   
   @Remove
   public void destroy() {}
}

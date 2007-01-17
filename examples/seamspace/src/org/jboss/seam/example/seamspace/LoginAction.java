package org.jboss.seam.example.seamspace;

import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.SeamSecurityManager;

/**
 * Login action
 * 
 * @author Shane Bryzak
 */
@Stateful
@Scope(ScopeType.SESSION)
@Synchronized
@Name("login")
public class LoginAction implements LoginLocal
{
   @Logger 
   private Log log;  
   
   @In(required = false) @Out(required = false)
   Member member;
   
   @In(create=true)
   private EntityManager entityManager;
   
   @Out(required = false)
   private Member authenticatedMember;
   
   public void login()
   {
      try
      {
         CallbackHandler cbh = SeamSecurityManager.instance().createCallbackHandler(
               member.getUsername(), member.getPassword());
         
         LoginContext lc = SeamSecurityManager.instance().createLoginContext(null, cbh);
         lc.login();
      }
      catch (LoginException ex)
      {
         FacesMessages.instance().add("Invalid login");
      }
   }
   
   public boolean authenticate(String username, String password, Set<String> roles) 
   {
      try
      {            
         authenticatedMember = (Member) entityManager.createQuery(
            "from Member where username = :username and password = :password")
            .setParameter("username", username)
            .setParameter("password", password)
            .getSingleResult();

         for (MemberRole mr : authenticatedMember.getRoles())
            roles.add(mr.getName());
         
         return true;
      }
      catch (NoResultException ex)
      {
         log.warn("Invalid username/password");
         return false;
      }      
   }   

   public void logout() 
   {
      Seam.invalidateSession();
   }

   @Remove @Destroy
   public void destroy() { }
}

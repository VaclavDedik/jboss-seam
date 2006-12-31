package org.jboss.seam.example.seamspace;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.Role;

/**
 * Authenticates the member against the database
 *
 * @author Shane Bryzak
 */
@Name("authenticatorAction")
public class AuthenticatorAction implements LoginModule
{
   private Subject subject;
   
   @In(create=true)
       private EntityManager entityManager;   
   
  public boolean abort() throws LoginException
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean commit() throws LoginException
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void initialize(Subject subject, CallbackHandler callbackHandler, 
         Map<String, ?> sharedState, Map<String, ?> options)
   {
      this.subject = subject;     
   }

   public boolean login() throws LoginException
   {
      try
      {         
        Member member = (Member) entityManager.createQuery(
            "from Member where username = :username and password = :password")
//            .setParameter("username", auth.getPrincipal().toString())
//            .setParameter("password", auth.getCredentials())
            .getSingleResult();

        Role[] roles = new Role[member.getRoles().size()];
        int idx = 0;
        for (MemberRole mr : member.getRoles())
          roles[idx++] = new Role(mr.getName());
        
//        return new UsernamePasswordToken(authentication.getPrincipal(),
//                                         authentication.getCredentials(), roles);
        return true;
      }
      catch (NoResultException ex)
      {
        throw new AuthenticationException("Invalid username/password");
      }
      catch (Exception ex)
      {
        throw new AuthenticationException("Unknown authentication error", ex);
      }
   }

   public boolean logout() throws LoginException
   {
      // TODO Auto-generated method stub
      return false;
   }
}

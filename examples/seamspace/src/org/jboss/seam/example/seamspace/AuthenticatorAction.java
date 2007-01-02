package org.jboss.seam.example.seamspace;

import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.UsernamePasswordToken;

/**
 * Authenticates the member against the database
 * 
 * @author Shane Bryzak
 */
public class AuthenticatorAction implements LoginModule
{
   private Subject subject;
   
   private UsernamePasswordToken token;

   public boolean abort() throws LoginException
   {
      return true;
   }

   public boolean commit() throws LoginException
   {
      Contexts.getSessionContext().set(Seam.getComponentName(Identity.class), 
            new UsernamePasswordToken(token.getName(), token.getCredentials(), 
                  token.getRoles()));
      return true;
   }

   public void initialize(Subject subject, CallbackHandler callbackHandler,
         Map<String, ?> sharedState, Map<String, ?> options)
   {
      this.subject = subject;

      Set<UsernamePasswordToken> principals = subject.getPrincipals(UsernamePasswordToken.class);
      
      if (principals.isEmpty())
         throw new AuthenticationException("No principal found in subject");
      
      token = principals.iterator().next();   
   }

   public boolean login() throws LoginException
   {
      EntityManager entityManager = null;
      try
      {
         InitialContext ctx = new InitialContext();
         EntityManagerFactory f = (EntityManagerFactory) ctx.lookup(
               "java:/seamspaceEntityManagerFactory");
         entityManager = f.createEntityManager();         
         
         Member member = (Member) entityManager
               .createQuery(
                     "from Member where username = :username and password = :password")
               .setParameter("username", token.getPrincipal().toString())
               .setParameter("password", token.getCredentials())
               .getSingleResult();

         for (MemberRole mr : member.getRoles())
            subject.getPrincipals().add(new Role(mr.getName()));
         
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
      finally
      {
         if (entityManager != null)
            entityManager.close();
      }
   }

   public boolean logout() throws LoginException
   {
      return true;
   }
}

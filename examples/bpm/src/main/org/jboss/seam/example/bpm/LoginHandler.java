package org.jboss.seam.example.bpm;

import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.contexts.Context;

@Stateless
@Name( "login" )
@Interceptor( SeamInterceptor.class )
public class LoginHandler implements Login
{

   @In @Out
   private User user;
   
   @Out(scope=ScopeType.SESSION)
   private String actorId;

   @PersistenceContext
   private EntityManager em;

   @In
   private FacesContext facesContext;

   @In
   private Context sessionContext;

   public String login()
   {
      List results = em.createQuery( "from User where username=:username and password=:password" )
            .setParameter( "username", user.getUsername() )
            .setParameter( "password", user.getPassword() )
            .getResultList();

      if ( results.size() == 0 )
      {
         facesContext.addMessage( null, new FacesMessage( "Invalid username/password" ) );
         return "login";
      }
      else
      {
         user = (User) results.get(0);
         sessionContext.set( "loggedIn", true );
         actorId = user.getUsername();
         return "main";
      }

   }

}

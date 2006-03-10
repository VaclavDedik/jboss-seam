package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.Interceptors;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.ejb.SeamInterceptor;

@Name("login")
@Stateful
@Scope(ScopeType.SESSION)
@Interceptors(SeamInterceptor.class)
public class LoginBean implements Login {

    @In(create=true)
    private EntityManager entityManager;
    
    @In(create=true)
    private Conversation conversation;

    private User instance = new User();
    
    public User getInstance() {
       return instance;
    }

    @In(create=true)
    private transient ResourceBundle resourceBundle;
 
    public String login() 
    {
       List results = entityManager.createQuery("from User where username=:username and password=:password")
          .setParameter("username", instance.getUsername())
          .setParameter("password", instance.getPassword())
          .getResultList();
       if ( results.size()==0 )
       {
          FacesContext.getCurrentInstance()
             .addMessage(null, new FacesMessage( resourceBundle.getString("InvalidLogin") ) );
          return null;
       }
       else
       {
          Contexts.getSessionContext().set("loggedIn", true);
          instance = (User) results.get(0);
          String outcome = conversation.redirect();
          return outcome==null ? "home" : outcome;
       }
    }
    
    public String logout()
    {
       Contexts.getSessionContext().remove("loggedIn");
       Seam.invalidateSession();
       return "home";
    }

}
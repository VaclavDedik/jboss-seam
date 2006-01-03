package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.util.ResourceBundle;

import javax.ejb.Interceptors;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.ejb.SeamInterceptor;


@Name("userEditor")
@Stateless
@Interceptors(SeamInterceptor.class)
public class UserEditorBean implements UserEditor {

    @In(create=true)
    private EntityManager entityManager;

    @Valid
    private User user = new User();
    
    public User getInstance() {
       return user;
    }

    @In
    private transient ResourceBundle resourceBundle;
 
    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String create() {
       if ( entityManager.find(User.class, user.getUsername())!=null )
       {
          FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(
                      resourceBundle.getString("User_username") + " " +
                      user.getUsername() + " " +
                      resourceBundle.getString("AlreadyExists")
                   )
             );
          return null;
       }
       entityManager.persist(user);
       return "login";
    }

}
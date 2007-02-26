package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.core.FacesMessages;


@Name("userEditor")
@Stateless
public class UserEditorBean implements UserEditor {

    @In
    private EntityManager entityManager;

    @Valid
    private User user = new User();
    
    public User getInstance() {
       return user;
    }
 
    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String create() {
       if ( entityManager.find(User.class, user.getUsername())!=null )
       {
          FacesMessages.instance().addFromResourceBundle("UserAlreadyExists");
          return null;
       }
       entityManager.persist(user);
       return "login";
    }

}
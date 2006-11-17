/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 

package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.core.Actor;
import org.jboss.seam.core.FacesMessages;

@Stateless
@Name("login")
public class LoginAction 
    implements Login,
               Serializable
{
    private static final String USER_VAR = "currentUser";

    @PersistenceContext
    private EntityManager em;

    @In Context sessionContext;
    
    @In Actor actor;
    
    String username = "";
    String password = "";

    public String getUserName() {
        return username;
    }
    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String login() {
        try {
            User found =  
                (User) em.createQuery("select u from User u where u.userName = :userName and u.password = :password")
                .setParameter("userName", username)
                .setParameter("password", password)
                .getSingleResult();

            sessionContext.set(USER_VAR, found);
            
            actor.setId(username);
            
            if (found instanceof Admin) {
                actor.getGroupActorIds().add("shippers");
                actor.getGroupActorIds().add("reviewers");
                return "admin";
            } else {
                return null; // redisplay the current page
            }
        } catch (Exception e) {
            // this message is lost in the session invalidation
            FacesMessages.instance().addFromResourceBundle("loginErrorPrompt");
            Seam.invalidateSession();
            return "home";
        }
    }

    public String logout() {
        Seam.invalidateSession();
        sessionContext.set(USER_VAR, null);
        sessionContext.set("loggedIn", null);
        return "logout";
    }

    private User currentUser() {
        return (User) sessionContext.get(USER_VAR);
    }

    public boolean isLoggedIn() {
        return currentUser() != null;
    }

    public boolean isCustomer() {
        User user = currentUser();
        return (user!=null) && (user instanceof Customer);
    }

    public boolean isAdmin() {
        User user = currentUser();
        return (user!=null) && (user instanceof Admin);
    }
    
    public String adminCheck() {
        return isAdmin() ? null : "home";
    }
}

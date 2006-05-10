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
    @PersistenceContext
    private EntityManager em;

    @In
    Context sessionContext;
    
    @In(create=true) 
    Actor actor;
    
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
                (User) em.createQuery("from User u where u.userName = :userName and u.password = :password")
                .setParameter("userName", username)
                .setParameter("password", password)
                .getSingleResult();

            sessionContext.set("currentUser", found);
            
            actor.setId(username);
            
            if (found instanceof Admin) {
                actor.getGroupActorIds().add("shippers");
                actor.getGroupActorIds().add("reviewers");
                return "admin";
            } else {
                return null; // redisplay the current page
            }
        } catch (Exception e) {
            FacesMessages.instance().addFromResourceBundle("loginErrorPrompt");
            Seam.invalidateSession();
            return null;
        }
    }

    public String logout() {
        Seam.invalidateSession();
        sessionContext.set("currentUser", null);
        sessionContext.set("loggedIn", null);
        return "logout";
    }

    public boolean isLoggedIn() {
        return sessionContext.get("currentUser") != null;
    }

}

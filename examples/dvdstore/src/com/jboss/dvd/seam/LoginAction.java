package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.core.Actor;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("login")
@Interceptor(SeamInterceptor.class)
@LoginIf
public class LoginAction 
    implements Login,
               Serializable
{
    @PersistenceContext(unitName="dvd")
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
    

    @LoginIf(outcome={"admin","customer"})
    public String login() {
        try {
            User found =  
                (User) em.createQuery("from User u where u.userName = :userName and u.password = :password")
                .setParameter("userName", username)
                .setParameter("password", password)
                .getSingleResult();

            System.out.println("user: " + found);

            sessionContext.set("currentUser", found);
            
            if (found instanceof Admin) {
                sessionContext.set("currentUserIsAdmin", true);
                actor.setId("admin");
                actor.getGroupActorIds().add("shipper");
                return "admin";
            } 
            else {
                return "customer";
            }
        } 
        catch (Exception e) {
            Utils.warnUser("loginErrorPrompt", null);    
            return "notok";
        }
    }
    
    public String logout() {
        Seam.invalidateSession();
        sessionContext.set("currentUser", null);
        sessionContext.set("loggedIn",    null);
        return "done";
    }

}

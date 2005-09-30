package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.contexts.Context;

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
    private Context sessionContext;
    
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
            System.out.println("LOGIN!");
            User found =  
                (User) em.createQuery("from User c where c.userName = :userName and " + 
                                      "c.password = :password")
                .setParameter("userName", username)
                .setParameter("password", password)
                .getSingleResult();

            System.out.println("USER: " + found);

            sessionContext.set("currentUser", found);
            
            if (found instanceof Admin) {
                return "admin";
            } else {
                return "customer";
            }
        } catch (Exception e) {
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

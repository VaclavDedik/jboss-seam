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

    @In @Out
    private Customer customer;

    @In
    private Context sessionContext;

    @LoginIf(outcome={"main"})
    public String login() {
        try { 
            System.out.println("FIND: " + customer.getUserName());
            Customer found =  
                (Customer) em.createQuery("from Customer c where c.userName = :userName and " + 
                                          "c.password = :password")
                .setParameter("userName", customer.getUserName())
                .setParameter("password", customer.getPassword())
                .getSingleResult();


            customer = found;
            return "main";
        } catch (Exception e) {
            System.out.println("!!FAIL");

            return "login";
        }
    }

    public String logout() {
        Seam.invalidateSession();
        return "done";
    }

}

/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.util.*;

import com.jboss.dvd.ejb.*;

import javax.annotation.*;
import javax.ejb.*;
import javax.persistence.*;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
// @Name("user")
// @LocalBinding(jndiBinding="user")
// @Interceptor(SeamInterceptor.class)
public class UserBean
    implements User
{
    @PersistenceContext(unitName="dvd")
    EntityManager em;

    private Customer customer = null;

    public Customer getCustomer() {
        if (customer == null) {
            String   user  = org.jboss.security.SecurityAssociation.getPrincipal().getName();
            customer =  (Customer) em.createQuery("from Customer c where c.userName = :userName")
                .setParameter("userName", user)
                .getSingleResult();
        }
        return customer;
    }

    public String logout() {
        Seam.invalidateSession();
        return "done";
    }
}

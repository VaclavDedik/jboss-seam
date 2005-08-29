
/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Name;

@Stateless
@Name("userinfo")
@JndiName("com.jboss.dvd.ejb.User")
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

}

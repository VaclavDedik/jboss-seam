/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.util.*;

import javax.ejb.*;
import javax.persistence.*;
import javax.annotation.*;

import com.jboss.dvd.ejb.*;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("history")
@LocalBinding(jndiBinding="history")
@Interceptor(SeamInterceptor.class)
public class PurchaseHistoryBean
    implements PurchaseHistory
{
    private static final int NUM_PRODUCTS = 10;

    @EJB
    User user;

    @PersistenceContext(unitName="dvd")
    EntityManager em;

    private List<Product> products = null;

    public List<Product> getRecentProducts() {
        System.out.println("USER:" + user);
        if (products == null) {
            products = em.createQuery("select i.product as p from Order o JOIN o.orderLines i " + 
                                      "JOIN FETCH i.product.relatedProduct " + 
                                      "where o.customer = :customer order by i.lineId DESC")
                .setParameter("customer", user.getCustomer())
                .setMaxResults(NUM_PRODUCTS)
                .getResultList();
        }

        System.out.println("RH: " + products);
        return products;
    }
}

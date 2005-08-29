/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("history")
@JndiName("com.jboss.dvd.ejb.PurchaseHistory")
@Interceptor(SeamInterceptor.class)
public class PurchaseHistoryAction
    implements PurchaseHistory
{
    private static final int NUM_PRODUCTS = 10;

    public PurchaseHistoryAction() {
        System.out.println("CREATE PURCHASE HISTORY: " + this);
    }

    @In(value="userinfo",create=true, alwaysDefined=true)
    User user;

    @PersistenceContext(unitName="dvd")
    EntityManager em;

    @Out(value="recentProducts")
    private List<Product> products = null;

    public String findProducts() {
        System.out.println("FIND - user=" + user);
        products = em.createQuery("select i.product as p from Order o JOIN o.orderLines i " + 
                                  "LEFT JOIN FETCH i.product.relatedProduct " + 
                                  "where o.customer = :customer order by i.lineId DESC")
            .setParameter("customer", user.getCustomer())
            .setMaxResults(NUM_PRODUCTS)
            .getResultList();

        return "main";
    }
}

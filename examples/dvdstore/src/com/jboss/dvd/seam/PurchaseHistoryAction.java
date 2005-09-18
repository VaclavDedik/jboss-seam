/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;


import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import org.jboss.seam.ejb.SeamInterceptor;



@Stateless
@Name("phistory")
@Intercept(InterceptionType.ALWAYS)
@Scope(ScopeType.EVENT)
@Interceptor(SeamInterceptor.class)
public class PurchaseHistoryAction
    implements PurchaseHistory
{
    private static final int NUM_PRODUCTS = 10;

    public PurchaseHistoryAction() {
        //System.out.println("CREATE PURCHASE HISTORY: " + this);
    }

    @In
    Customer customer;

    @PersistenceContext(unitName="dvd")
    EntityManager em;

    private List<Product> products = null;

    public List<Product> getRecentProducts() {
        //System.out.println("FIND[" + customer + "] products: " + products);
        if (products == null) {
            products = em.createQuery("select i.product as p from Order o JOIN o.orderLines i " + 
                                      "LEFT JOIN FETCH i.product.relatedProduct " + 
                                      "where o.customer = :customer order by i.lineId DESC")
                .setParameter("customer", customer)
                .setMaxResults(NUM_PRODUCTS)
                .getResultList();
        }

        return products;
    }

}

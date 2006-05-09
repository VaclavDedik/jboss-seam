/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 

package com.jboss.dvd.seam;

import java.io.Serializable;
import java.util.*;

import javax.ejb.*;
import javax.persistence.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.*;

@Stateful
@Name("bestsellers")
public class BestSellersBean
    implements BestSellers,
               Serializable
{
    private static int MAX_RESULTS = 8;

    @In(create=true)
    ShoppingCart cart;

    @PersistenceContext
    EntityManager em;

    @DataModel
    List<Product> topProducts;

    @DataModelSelection
    @Out(required=false)
    Product dvd;

    @Factory("topProducts")
    public void doSearch() {
        topProducts = em.createQuery("from Product p order by p.inventory.sales DESC")
                        .setMaxResults(MAX_RESULTS)
                        .getResultList();
    }

    /**
     *  Add the selected DVD to the cart
     */
    public void addToCart() { 
        cart.addProduct(dvd,1);
    }

    @Destroy 
    @Remove
    public void destroy() {}
    
}

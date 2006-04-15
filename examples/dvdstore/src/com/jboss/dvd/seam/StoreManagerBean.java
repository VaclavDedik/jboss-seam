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
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;

@Stateless
@Name("stats")
public class StoreManagerBean
    implements StoreManager,
               Serializable
{  
    @PersistenceContext
    EntityManager em;

    public int getNumberOrders() {
        return (Integer) em.createQuery("select count(o) from Order o where o.status != :status")
            .setParameter("status", Order.Status.CANCELLED)
            .getSingleResult();
    }

    public double getTotalSales() {
        try {
            return (Float) em.createQuery("select sum(o.totalAmount) from Order o where o.status != :status")
                .setParameter("status", Order.Status.CANCELLED)
                .getSingleResult();
        } 
        catch (EntityNotFoundException e) {
            return 0.0;
        }
    }

    public int getUnitsSold() {
        try {
            return (Integer) em.createQuery("select sum(i.sales) from Inventory i").getSingleResult();
        } 
        catch (EntityNotFoundException e) {
            return 0;
        }
    }

    public int getTotalInventory() {
        try {
            return (Integer) em.createQuery("select sum(i.quantity) from Inventory i").getSingleResult();
        } 
        catch (EntityNotFoundException e) {
            return 0;
        }
    }

}

/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;

import com.jboss.dvd.seam.Order.Status;

@Stateful
@Name("showorders")
public class ShowOrdersAction
    implements ShowOrders,
               Serializable
{
    @In(value="currentUser",required=false)
    Customer customer;

    @PersistenceContext
    EntityManager em;

    @DataModel
    List<Order> orders;    

    @Out(value="myorder", required=false, scope=ScopeType.CONVERSATION)
    Order order;

    @DataModelSelectionIndex
    int index;

    @Begin @Factory("orders")
    public String findOrders() {
        orders = em.createQuery("from Order o where o.customer = :customer")
                   .setParameter("customer", customer)
                   .getResultList();
        order = null;

        return "showorders";
    }


    public String cancelOrder() {
        if (order.getStatus() != Status.OPEN) {
            return null;
        }

        order = em.merge(order);
        order.setStatus(Status.CANCELLED);
        
        return findOrders();
    }

    public String detailOrder() {
        order = em.merge(orders.get(index));
        order.getOrderLines();

        return "showorders";
    }

    @End
    public String reset() {
        return null;
    }

    @Destroy 
    @Remove
    public void destroy() {}
    
}

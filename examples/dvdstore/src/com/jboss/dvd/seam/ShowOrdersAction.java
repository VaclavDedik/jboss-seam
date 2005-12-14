/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Out;

import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;

import org.jboss.seam.ejb.SeamInterceptor;

import org.jboss.seam.jsf.ListDataModel;
//import javax.faces.model.ListDataModel;

import com.jboss.dvd.seam.Order.Status;

@Stateful
@Name("showorders")
@Conversational(ifNotBegunOutcome="showorders")
@LoggedIn
@Interceptor(SeamInterceptor.class)
public class ShowOrdersAction
    implements ShowOrders,
               Serializable
{
    @In(value="currentUser",required=false)
    Customer customer;

    @PersistenceContext(unitName="dvd")
    EntityManager em;

    ListDataModel model;
    //@DataModel
    List<Order> orders;    

    @Out(value="myorder", required=false)
    Order order;

    //@DataModelSelectionIndex
    //int index;

    public ListDataModel getOrders() {
        return model;
    }


    @Begin
    public String findOrders() {
        orders = em.createQuery("from Order o where o.customer = :customer")
                   .setParameter("customer", customer)
                   .getResultList();
        model = new ListDataModel(orders);
        order = null;

        return "showorders";
    }


    public String cancelOrder() {
        if (order.getStatus() != Status.OPEN) {
            System.out.println("Wrong state for CANCEL");
            return null;
        }

        order = em.merge(order);
        order.setStatus(Status.CANCELLED);
        
        return findOrders();
    }

    public String detailOrder() {
        int index = model.getRowIndex();
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
    public void destroy() {
    }
}

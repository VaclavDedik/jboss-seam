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

import com.jboss.dvd.seam.Order.Status;

@Stateful
@Name("manageorders")
@Conversational(ifNotBegunOutcome="admin")
@LoggedIn
@Interceptor(SeamInterceptor.class)
public class ManageOrdersAction
    implements ManageOrders,
               Serializable
{
    @In(value="currentUser",required=false)
    Admin admin;

    @PersistenceContext(unitName="dvd")
    EntityManager em;

    ListDataModel model;
    List<Order> orders;

    @Out(required=false)
    Order order;

    @Begin
    public String findOrders() {
        orders = em.createQuery("from Order o where o.status = :status")
                   .setParameter("status", Status.OPEN.ordinal())
                   .getResultList();
        model = new ListDataModel(orders);
        order = null;

        return "manageorders";
    }

    public ListDataModel getOpenOrders() {
        return model;
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

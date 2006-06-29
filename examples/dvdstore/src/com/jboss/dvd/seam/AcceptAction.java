/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;


@Stateful
@Name("accept")
@Conversational(ifNotBegunOutcome="admin", initiator=true)
public class AcceptAction
    implements Accept,
               Serializable
{
    @In(required=false, value="currentUser")
    Admin admin;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;

    @Out(required=false,scope=ScopeType.CONVERSATION)
    Order order;

    @In(required=false)
    Long orderId;
    
    // this is a guard action on the shipping page to force a redirect
    public String ping() {
        return null;
    }

    @BeginTask
    public String viewTask() {
        order = (Order) em.createQuery("from Order o join fetch o.orderLines where o.orderId = :orderId")
            .setParameter("orderId", orderId)
            .getSingleResult();
        return "accept";
    }

    @EndTask(transition="approve")
    public String accept() {
        order.process();
        return "admin";
    }

    @EndTask(transition="reject")
    public String reject() {
        order.cancel();
        return "admin";
    }

    @Destroy 
    @Remove
    public void destroy() {}
}

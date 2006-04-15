/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.ejb.Interceptors;
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
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.util.Transactions;

@Stateful
@Name("ship")
@Conversational(ifNotBegunOutcome="admin")
@Interceptors(SeamInterceptor.class)
public class ShipAction
    implements Ship,
               Serializable
{
    @In(value="currentUser")
    Admin admin;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;

    @Out(scope=ScopeType.CONVERSATION)
    Order order;

    @In
    Long orderId;

    String track;

    public String getTrack() {
        return track;
    }
    public void setTrack(String track) {
        this.track=track;
    }

    @BeginTask
    public String viewTask() {
        order = (Order) em.find(Order.class, orderId);
        return "ship";
    }

    @EndTask
    public String ship() {
        if (track == null || track.length()==0) {
            // invalid message
            return null;
        }

        order.ship(track);
        
        return "admin";
    }

    @Destroy 
    @Remove
    public void destroy() {}
}

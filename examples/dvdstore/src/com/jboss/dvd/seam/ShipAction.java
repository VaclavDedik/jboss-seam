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
import org.jboss.seam.annotations.*;

@Stateful
@Name("ship")
@Conversational(ifNotBegunOutcome="admin",initiator=true)
public class ShipAction
    implements Ship,
               Serializable
{
    @In(value="currentUser", required=false)
    Admin admin;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    @Out(required=false, scope=ScopeType.CONVERSATION)
    Order order;

    @In(required=false)
    Long orderId;

    String track;

    // this is a guard action on the shipping page to force a redirect
    public String ping() {
        return null;
    }

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

    @Destroy @Remove
    public void destroy() { }
}

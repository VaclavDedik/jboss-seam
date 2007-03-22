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

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

@Stateful
@Name("ship")
public class ShipAction
    implements Ship,
               Serializable
{
    private static final long serialVersionUID = -5284603520443473953L;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    @Out(required=false, scope=ScopeType.CONVERSATION)
    Order order;
    
    String track;

    @NotNull
    @Length(min=4,max=10)
    public String getTrack() {
        return track;
    }
    public void setTrack(String track) {
        this.track=track;
    }

    @BeginTask
    public String viewTask() {          
        order = (Order) Component.getInstance("workingOrder");
        return "ship";
    }
    
    @EndTask
    public String ship() {        
        order.ship(track);
        
        return "admin";
    }

    @Destroy @Remove
    public void destroy() { }
}

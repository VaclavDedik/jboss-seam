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
import javax.persistence.PersistenceContextType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.core.ManagedJbpmContext;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.jboss.dvd.seam.Order.Status;

@Stateful
@Name("showorders")
public class ShowOrdersAction
    implements ShowOrders,
               Serializable
{
    @In(value="currentUser",required=false)
    Customer customer;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;

    @DataModel
    List<Order> orders;    

    @DataModelSelection
    @Out(value="myorder", required=false, scope=ScopeType.CONVERSATION)
    Order order;

    @Begin @Factory("orders")
    public String findOrders() {
        orders = em.createQuery("select o from Order o where o.customer = :customer")
            .setParameter("customer", customer)
            .getResultList();

        order = null;

        return "showorders";
    }


    public String cancelOrder() {
       
        em.refresh(order);
       
        if ( order.getStatus() != Status.OPEN ) {
            return null;
        }

        order.setStatus(Status.CANCELLED);
        
        JbpmContext context = ManagedJbpmContext.instance();

        ProcessInstance pi = (ProcessInstance) context.getSession()
            .createQuery("select pi from LongInstance si join si.processInstance pi " +
                         "where si.name = 'orderId' and si.value = :orderId")
            .setLong("orderId", order.getOrderId())
            .uniqueResult();
        
        pi.signal("cancel");
        
        context.save(pi);
        
        return findOrders();
    }

    public String detailOrder() {
        em.refresh(order);
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

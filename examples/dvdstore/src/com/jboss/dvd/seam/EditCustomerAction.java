/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;


import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.ejb.Interceptor;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

import org.hibernate.validator.Valid;

@Stateless
@Name("editCustomer")
@Scope(ScopeType.EVENT)
@Interceptor(SeamInterceptor.class)
@LoginIf
public class EditCustomerAction
    implements EditCustomer
{
    @PersistenceContext(unitName="dvd")
    EntityManager em;
    
    @Resource
    SessionContext ctx;

    @In 
    @Out
    @Valid
    Customer customer;

    public EditCustomerAction() {

    }
    
    public Customer getCustomer() {
        return customer;
    }

    public Map<String,Integer> getCreditCardTypes() {
        Map<String,Integer> map = new TreeMap<String,Integer>();
        for (int i=1; i<=5; i++) {
            map.put(Customer.cctypes[i-1], i);
        }
        return map;
    }

    public String startEdit() {
        return "newcustomer";
    }

    @IfInvalid(outcome=Outcome.REDISPLAY)
    @LoginIf(outcome={"main"})
    public String create() {
        try {
            em.persist(customer);            
            return "main";
        }  catch (RuntimeException e) {
            ctx.setRollbackOnly();

            System.out.println("Could not create customer");
            Utils.warnUser("createCustomerError", null);
            return null;
        }
    }



}

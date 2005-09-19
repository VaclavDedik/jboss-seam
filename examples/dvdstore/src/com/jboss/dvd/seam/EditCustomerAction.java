/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.ejb.Interceptor;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.jboss.seam.contexts.Context;
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
    private Context sessionContext;

    @In(create=true)
    @Out
    @Valid
    Customer customer;

    String password = null;    

    public EditCustomerAction() {

    }
    
    public void   setPasswordVerify(String password) {
        this.password = password;
    }
    public String getPasswordVerify() {
        return password;
    }

    public Map<String,Integer> getCreditCardTypes() {
        Map<String,Integer> map = new TreeMap<String,Integer>();
        for (int i=1; i<=5; i++) {
            map.put(Customer.cctypes[i-1], i);
        }
        return map;
    }

    private boolean passwordsMatch() {
        String customerpass = customer.getPassword();

        return (password != null)
            && (customerpass != null) 
            && (customerpass.equals(password));
    }


    @IfInvalid(outcome=Outcome.REDISPLAY)
    @LoginIf(outcome={"ok"})
    public String create() {
        if (!passwordsMatch()) {
                Utils.warnUser("createCustomerPasswordError", null);
                return null;
        }

        try {
            List existing  =  
                em.createQuery("from Customer c where c.userName = :userName")
                .setParameter("userName", customer.getUserName())
                .getResultList();


            if (existing.size()>1) {
                Utils.warnUser("createCustomerExistingError", null);

                return null;
            }

            em.persist(customer);
            sessionContext.set("currentUser", customer);
            System.out.println("Created: " + customer);
            return "ok";
        }  catch (RuntimeException e) {
            System.out.println("not created");
            ctx.setRollbackOnly();

            Utils.warnUser("createCustomerError", null);
            return null;
        }
    }



}

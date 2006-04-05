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
import javax.ejb.Interceptors;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.core.Actor;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("editCustomer")
@Scope(ScopeType.EVENT)
@Interceptors(SeamInterceptor.class)
public class EditCustomerAction
    implements EditCustomer
{
    @PersistenceContext
    EntityManager em;
    
    @Resource
    SessionContext ctx;

    @In
    Context sessionContext;

    @In(create=true)
    @Valid
    Customer customer;
    
    @In(create=true)
    FacesMessages facesMessages;

    String password = null;
    
    public void setPasswordVerify(String password) {
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
    public String create() {
        if (!passwordsMatch()) {
                facesMessages.addFromResourceBundle("createCustomerPasswordError");
                return null;
        }

        try {
            List existing  =  
                em.createQuery("from Customer c where c.userName = :userName")
                .setParameter("userName", customer.getUserName())
                .getResultList();


            if (existing.size()>0) {
                facesMessages.addFromResourceBundle("createCustomerExistingError");
                return null;
            }

            em.persist(customer);
            sessionContext.set("currentUser", customer);
            Actor.instance().setId(customer.getUserName());
            
            facesMessages.addFromResourceBundle("createCustomerSuccess");
            return "success";
        }  
        catch (RuntimeException e) {
            ctx.setRollbackOnly();

            facesMessages.addFromResourceBundle("createCustomerError");
            return null;
        }
    }

}

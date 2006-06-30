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
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.*;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.core.Actor;
import org.jboss.seam.core.FacesMessages;

@Stateful
@Name("editCustomer")
@Conversational(ifNotBegunOutcome="browse",initiator=true)
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
    @Out
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


    @Begin(nested=true, pageflow="newuser") 
    public void startEdit() {
    }

    public boolean isValidNamePassword() {
        boolean ok = true;
        if (!isUniqueName()) {
            facesMessages.add("userName", "This name is already in use");
            ok = false;
        }
        if (!isPasswordsMatch()) {
            facesMessages.add("passwordVerify", "Must match password field");
            ok = false;
        }
        return ok;
    }

    private boolean isUniqueName() {
        String name = customer.getUserName();
        if (name == null) return true;

        List<Customer> results = em.createQuery("from Customer c where c.userName = :name")
            .setParameter("name", name)
            .getResultList();

        return results.size() == 0;
    }


    private boolean isPasswordsMatch() {
        String customerpass = customer.getPassword();

        return (password != null)
            && (customerpass != null) 
            && (customerpass.equals(password));
    }

    public String saveUser() {
        if (!isValidNamePassword()) {
            facesMessages.add("User name #{customer.userName} is not unique");
            return null;
        }

        try {
            em.persist(customer);
            sessionContext.set("currentUser", customer);
            Actor.instance().setId(customer.getUserName());
            
            facesMessages.addFromResourceBundle("createCustomerSuccess");
            return "success";
        } catch (InvalidStateException e) {
            InvalidValue[] vals = e.getInvalidValues();
            for (InvalidValue val: vals) {
                facesMessages.add(val);
            }

            return null;
        } catch (RuntimeException e) {
            ctx.setRollbackOnly();

            facesMessages.addFromResourceBundle("createCustomerError");

            return null;
        }
    }

    
    public Map<String,Integer> getCreditCardTypes() {
        Map<String,Integer> map = new TreeMap<String,Integer>();
        for (int i=1; i<=5; i++) {
            map.put(Customer.cctypes[i-1], i);
        }
        return map;
    }
   

    public String ping() { return null; }

    @Destroy @Remove
    public void destroy() {
    }
}

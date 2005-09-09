/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;


import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.ejb.Interceptor;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.security.Util;


@Stateless
@Name("editCustomer")
@JndiName("com.jboss.dvd.ejb.EditCustomer")
@Interceptor(SeamInterceptor.class)
public class EditCustomerBean
    implements EditCustomer
{
    @PersistenceContext(unitName="dvd")
    EntityManager em;
    
    @Resource
    SessionContext ctx;

    Customer customer;

    String  password = "";
    int     month    = 1;
    int     year     = 2005;
    Integer cardType = new Integer(1);

    public EditCustomerBean() {
        customer = new Customer();
        customer.setCreditCard("000-0000-0000");
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

    public Integer getCreditCardType() {
        return cardType;
    }
    public void setCreditCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public int getCreditCardMonth() {
        return month;
    }
    public void setCreditCardMonth(int month) {
        this.month=month;
    }
    
    public int getCreditCardYear() {
        return year;
    }
    public void setCreditCardYear(int year) {
         this.year=year;
     }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
         this.password = password;
    }
    
    public String create() {
        try {
            customer.setCreditCardExpiration(year + "/" + (month < 10 ? "0" : "") + month);

            customer.setCreditCardType(cardType);
            customer.setHashedPassword(Util.createPasswordHash("MD5", 
                                                               Util.BASE64_ENCODING, 
                                                               null, 
                                                               null, 
                                                               password));
            
            em.persist(customer);            

            return "newcustomerok";
        } 
        catch (RuntimeException e) {
            System.out.println("Could not create customer");
            e.printStackTrace();
            Utils.warnUser("createCustomerError", null);
            ctx.setRollbackOnly();
            return null;
        }
    }


    public String getRandomUser() {
        //int random = (new Random()).nextInt(20000) + 1;
        //return "user" + random;
        return "user1";
    }

    public String getRandomUserPassword() {
        return "password";
    }

}

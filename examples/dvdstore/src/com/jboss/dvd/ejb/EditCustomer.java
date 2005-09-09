/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.util.Map;

import javax.ejb.Local;

@Local
public interface EditCustomer
{
    public String create();
    public String getRandomUser();
    public String getRandomUserPassword();

    public Map<String,Integer> getCreditCardTypes();

    public int getCreditCardMonth();
    public void setCreditCardMonth(int month);
    public int getCreditCardYear();
    public void setCreditCardYear(int year);
    public Integer getCreditCardType();
    public void setCreditCardType(Integer type);
    public String getPassword();
    public void setPassword(String password);

    public Customer getCustomer();
}

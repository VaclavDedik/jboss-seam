/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.util.Map;

import javax.ejb.Local;

@Local
public interface EditCustomer
{
    public Map<String,Integer> getCreditCardTypes();

    public void   setPasswordVerify(String password);
    public String getPasswordVerify();

    public String create();
}

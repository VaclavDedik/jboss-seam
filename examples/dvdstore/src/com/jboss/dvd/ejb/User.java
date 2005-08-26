/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import javax.ejb.Local;

@Local
public interface User
{
    public Customer getCustomer();
    public String logout();
}

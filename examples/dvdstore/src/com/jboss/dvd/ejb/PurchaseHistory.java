/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.util.List;

import javax.ejb.Local;

@Local
public interface PurchaseHistory {
    public List<Product> getRecentProducts();
}

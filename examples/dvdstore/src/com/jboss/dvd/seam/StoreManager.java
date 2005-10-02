/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import javax.ejb.Local;

@Local
public interface StoreManager
{  
    public int    getNumberOrders();
    public int    getUnitsSold();
    public int    getTotalInventory();
    public double getTotalSales();
}

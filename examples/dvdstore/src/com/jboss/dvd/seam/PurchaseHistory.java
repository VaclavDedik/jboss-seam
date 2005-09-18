/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.util.List;

public interface PurchaseHistory {
    public List<Product> getRecentProducts();
}

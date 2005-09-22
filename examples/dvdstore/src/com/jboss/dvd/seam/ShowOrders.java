/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import javax.faces.model.DataModel;

public interface ShowOrders {
    public String findOrders();

    public String detailOrder();
    public String cancelOrder();

    public String reset();


    public DataModel getOrders();

    public void destroy();
}

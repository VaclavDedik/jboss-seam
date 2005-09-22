/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import javax.faces.model.DataModel;

public interface ManageOrders {
    public String findOrders();
//     public String assignOrder();
//     public String shipOrder();

    public DataModel getOpenOrders();

    public String reset();
    public void destroy();
}

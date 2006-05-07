/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

public interface Checkout
{
    public void startCheckout();
    public void createOrder();
    public void submitOrder();

    public void destroy();
}

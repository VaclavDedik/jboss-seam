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
public interface ShoppingCart
{
    public boolean getIsEmpty();

    public void addProduct(Product product, int quantity);
    public List<SelectableItem<OrderLine>> getCart();
    public float getSubtotal();
    public float getTax();
    public float getTotal();
    public String updateCart();
    public String resetCart();
    public String purchase();
    public Order getOrder();
}

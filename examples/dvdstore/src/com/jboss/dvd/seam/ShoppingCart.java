/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.util.List;
import java.util.Map;

public interface ShoppingCart
{
    public boolean getIsEmpty();

    public void addProduct(Product product, int quantity);
    public List<OrderLine> getCart();
    public Map getCartSelection();
    public float getSubtotal();
    public float getTax();
    public float getTotal();
    public void updateCart();
    public String purchase();

    public void destroy();
}

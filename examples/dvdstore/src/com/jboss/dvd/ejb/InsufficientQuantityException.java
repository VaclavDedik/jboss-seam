/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.util.List;

import javax.ejb.*;

@ApplicationException(rollback=true)
public class InsufficientQuantityException
    extends Exception
{
    List<Product> products = null;

    public InsufficientQuantityException(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }
}

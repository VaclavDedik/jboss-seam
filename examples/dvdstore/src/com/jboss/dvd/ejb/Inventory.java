/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

@Entity
@Name("xinventory")
@Table(name="INVENTORY")
public class Inventory 
    implements Serializable
{
    int     quantity;
    int     sales;
    long    inventoryId;
    Product product;

    @Id(generate=GeneratorType.AUTO)
    @Column(name="INV_ID")
    public long getInventoryId() {
        return inventoryId;
    }
    public void setInventoryId(long id) {
        this.inventoryId = id;
    }

    @OneToOne(optional=false)
    @JoinColumn(name="PROD_ID")
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    @Column(name="QUAN_IN_STOCK",nullable=false)
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Column(name="SALES",nullable=false)
    public int getSales() {
        return sales;
    }
    public void setSales(int sales) {
        this.sales = sales;
    }


    public boolean order(int howmany) {
        if (howmany > quantity) {
            return false;
        }

        quantity -= howmany;
        sales += howmany;

        return true;
    }
}

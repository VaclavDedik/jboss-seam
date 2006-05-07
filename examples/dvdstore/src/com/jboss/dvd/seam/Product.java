/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="PRODUCTS")
public class Product
    implements Serializable
{
    long productId;
    String title;
    String actor;
    String description;
    float price;

    Category  category;
    Inventory inventory;

    @Id @GeneratedValue
    @Column(name="PROD_ID")
    public long getProductId() {
        return productId;
    }                    
    public void setProductId(long id) {
        this.productId = id;
    }     

    @OneToOne(fetch=FetchType.LAZY,mappedBy="product")
    public Inventory getInventory() {
        return inventory;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    @ManyToOne
    @JoinColumn(name="CATEGORY",nullable=false)
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    
    @Column(name="TITLE",nullable=false,length=50)
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name="DESCRIPTION",length=1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="ACTOR",nullable=false,length=50)
    public String getActor() {
        return actor;
    }
    public void setActor(String actor) {
        this.actor = actor;
    }

    @Column(name="PRICE",nullable=false,precision=12,scale=2)
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price=price;
    }
}

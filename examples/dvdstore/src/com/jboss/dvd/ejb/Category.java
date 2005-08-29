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
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jboss.seam.annotations.Name;

@Entity
@Name("xcategory")
@Table(name="CATEGORIES")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class Category
    implements Serializable
{
    int    category;  // should really be a byte
    String categoryName;

    @Id(generate=GeneratorType.AUTO)
    @Column(name="CATEGORY")
    public int getCategory() {
        return category;
    }
    public void setCategory(int category) {
        this.category = category;
    }

    @Column(name="CATEGORYNAME",nullable=false,unique=true,length=50)
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

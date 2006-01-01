/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
//@Name("category")
@Table(name="CATEGORIES")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class Category
    implements Serializable
{
    int    id;
    String name;

    @Id(generate=GeneratorType.AUTO)
    @Column(name="CATEGORY")
    public int getCategoryId() {
        return id;
    }
    public void setCategoryId(int id) {
        this.id = id;
    }

    @Column(name="NAME",nullable=false,unique=true,length=50)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof Category)) {
            return false;
        }

        Category otherCategory = (Category) other;
        return (getCategoryId() == otherCategory.getCategoryId());
    }

}

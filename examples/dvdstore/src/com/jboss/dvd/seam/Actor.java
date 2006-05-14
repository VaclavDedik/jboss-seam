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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ACTORS")
public class Actor
    implements Serializable
{
    long id;
    String name;

    @Id @GeneratedValue
    @Column(name="ID")
    public long getId() {
        return id;
    }                    
    public void setId(long id) {
        this.id = id;
    }     

    @Column(name="NAME", length=50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

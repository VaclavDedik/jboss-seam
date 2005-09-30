/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

@Entity
@Table(name="USERS")
@Name("user")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE,
             discriminatorType=DiscriminatorType.STRING)
public abstract class User
    implements Serializable
{
    long    customerId;

    String  userName;
    String  password;

    String  firstName;
    String  lastName;

    @Id(generate=GeneratorType.AUTO)
    @Column(name="USERID")
    public long getCustomerId() {
        return customerId;
    }                    
    public void setCustomerId(long id) {
        this.customerId = id;
    }     

    @Column(name="USERNAME",unique=true,nullable=false,length=50)    
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name="PASSWORD",nullable=false,length=50)    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="FIRSTNAME",length=50)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @Column(name="LASTNAME",length=50)    
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

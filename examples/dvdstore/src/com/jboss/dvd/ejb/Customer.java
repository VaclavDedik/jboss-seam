/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.ejb;

import javax.ejb.*;
import javax.persistence.*;

import java.io.Serializable;

import org.jboss.seam.annotations.Name;

@Entity
@Table(name="CUSTOMERS")
@Name("xcustomer")
public class Customer
    implements Serializable
{
    public static String[] cctypes = {"MasterCard", "Visa", "Discover", "Amex", "Dell Preferred"}; 

    long   customerId;
    String userName;
    String password;
    String firstName;
    String lastName;
    String address1;
    String address2;
    String city;
    String state;
    String zip;  
    String country;
    int    region;
    String email;
    String phone;
    int    creditCardType;
    String creditCard;
    String creditCardExpiration;
    int    age;
    long   income;
    String gender;

    public Customer() {
    }


    @Id(generate=GeneratorType.AUTO)
    @Column(name="CUSTOMERID")
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
    public String getHashedPassword() {
        return password;
    }
    public void setHashedPassword(String password) {
        this.password = password;
    }


    @Column(name="FIRSTNAME",nullable=false,length=50)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @Column(name="LASTNAME",nullable=false,length=50)    
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name="ADDRESS1",nullable=false,length=50)    
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    @Column(name="ADDRESS2",length=50)
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    @Column(name="CITY",nullable=false,length=50)  
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    @Column(name="STATE",length=50)
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    @Column(name="ZIP", length=50)
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(name="COUNTRY",nullable=false,length=50)
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name="REGION",nullable=false)
    public int getRegion() {
        return region;
    }
    public void setRegion(int region) {
        this.region = region;
    }

    @Column(name="EMAIL",length=50)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name="PHONE",length=50)
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name="AGE")    
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    @Column(name="INCOME")    
    public long getIncome() {
        return income;
    }
    public void setIncome(long income) {
        this.income = income;
    }

    @Column(name="GENDER", length=1)
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Column(name="CREDITCARDTYPE")    
    public int getCreditCardType() {
        return creditCardType;
    }
    public void setCreditCardType(int type) {
        this.creditCardType = type;
    }

    @Transient public String getCreditCardTypeString() {
        if (creditCardType<1 || creditCardType>cctypes.length) {
            return "";
        }
        return cctypes[creditCardType-1];
    }

    @Column(name="CREDITCARD",nullable=false,length=50)
    public String getCreditCard() {
        return creditCard;
    }
    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    @Column(name="CREDITCARDEXPIRATION",nullable=false,length=50)
    public String getCreditCardExpiration() {
        return creditCardExpiration;
    }
    public void setCreditCardExpiration(String creditCardExpiration) {
        this.creditCardExpiration = creditCardExpiration;
    }


}

/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;

@Entity
@Name("customer")
@DiscriminatorValue("customer")
public class Customer
    extends User
    implements Serializable
{
    public static String[] cctypes = {"MasterCard", "Visa", "Discover", "Amex", "Dell Preferred"}; 

    String  address1;
    String  address2;
    String  city;
    String  state;
    String  zip;  
    String  country;
    Integer region;
    String  email;
    String  phone;

    Integer creditCardType = 1;
    String  creditCard     = "000-0000-0000";
    int     ccMonth        = 1;
    int     ccYear         = 2005;


    public Customer() {
    }


    @Column(name="ADDRESS1",length=50)    
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
    @Column(name="CITY",length=50)  
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
    //@Length(min=5, max=5)
    //@NotNull
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(name="COUNTRY",length=50)
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name="REGION")
    public Integer getRegion() {
        return region;
    }
    public void setRegion(Integer region) {
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


    @Column(name="CREDITCARDTYPE")    
    public Integer getCreditCardType() {
        return creditCardType;
    }
    public void setCreditCardType(Integer type) {
        this.creditCardType = type;
    }

    @Transient public String getCreditCardTypeString() {
        if (creditCardType<1 || creditCardType>cctypes.length) {
            return "";
        }
        return cctypes[creditCardType-1];
    }

    @Column(name="CC_NUM", length=50)
    public String getCreditCard() {
        return creditCard;
    }
    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    @Column(name="CC_MONTH", length=50)
    public int getCreditCardMonth() {
        return ccMonth;
    }
    public void setCreditCardMonth(int ccMonth) {
        this.ccMonth = ccMonth;
    }

    @Column(name="CC_YEAR", length=50)
    public int getCreditCardYear() {
        return ccYear;
    }
    public void setCreditCardYear(int ccYear) {
        this.ccYear = ccYear;
    }

    @Transient
    public String getCreditCardExpiration() {
        return "" + ccMonth + "/" + ccYear;
    }

    public String toString() {
        return "Customer#" + getId() + "(" + userName + ")";
    }

}

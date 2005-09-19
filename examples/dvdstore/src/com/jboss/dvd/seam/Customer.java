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
import javax.persistence.Transient;

//import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.Scope;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name="CUSTOMERS")
@Name("customer")
//@Scope(ScopeType.SESSION)
public class Customer
    implements Serializable
{
    public static String[] cctypes = {"MasterCard", "Visa", "Discover", "Amex", "Dell Preferred"}; 

    long    customerId;
    String  userName;
    String  password;
    String  firstName;
    String  lastName;
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
    Integer age;
    Long    income;
    String  gender;

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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
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
    //@Length(min=5, max=5)
    //@NotNull
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

    @Column(name="AGE")    
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    @Column(name="INCOME")    
    public Long getIncome() {
        return income;
    }
    public void setIncome(Long income) {
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

    @Column(name="CC_NUM", nullable=false, length=50)
    public String getCreditCard() {
        return creditCard;
    }
    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    @Column(name="CC_MONTH", nullable=false, length=50)
    public int getCreditCardMonth() {
        return ccMonth;
    }
    public void setCreditCardMonth(int ccMonth) {
        this.ccMonth = ccMonth;
    }

    @Column(name="CC_YEAR", nullable=false, length=50)
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
        return "Customer#" + customerId + "(" + userName + ")";
    }

}

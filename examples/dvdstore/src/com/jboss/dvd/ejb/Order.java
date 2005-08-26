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
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.jboss.seam.annotations.Name;

@Entity
@Name("xorder")
@Table(name="ORDERS")
public class Order
    implements Serializable
{
    long orderId;
    Date orderDate;
    Customer customer;
    float netAmount;
    float tax;
    float totalAmount;
    List<OrderLine> orderLines;

    @Id(generate=GeneratorType.AUTO)
    @Column(name="ORDERID")
    public long getOrderId() {
        return orderId;
    }                    
    public void setOrderId(long id) {
        this.orderId = id;
    }     

    @Column(name="ORDERDATE",nullable=false)
    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date date) {
        this.orderDate = date;
    }

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL)
    public List<OrderLine> getOrderLines() {
        return orderLines;
    }
    public void setOrderLines(List<OrderLine> lines) {
        this.orderLines = lines;
    }
    
    @ManyToOne
    @JoinColumn(name="CUSTOMERID")
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Column(name="NETAMOUNT",nullable=false,precision=12,scale=2)
    public float getNetAmount() {
        return netAmount;
    }
    public void setNetAmount(float amount) {
        this.netAmount = amount;
    }

    @Column(name="TAX",nullable=false,precision=12,scale=2)
    public float getTax() {
        return tax;
    }
    public void setTax(float amount) {
        this.tax = amount;
    }

    @Column(name="TOTALAMOUNT",nullable=false,precision=12,scale=2)
    public float getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(float amount) {
        this.totalAmount = amount;
    }

}

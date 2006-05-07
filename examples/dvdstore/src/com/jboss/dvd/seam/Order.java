/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="ORDERS")
public class Order
    implements Serializable
{
    public enum Status {OPEN,CANCELLED,PROCESSING,SHIPPED};

    long orderId;
    Date orderDate;
    Customer customer;
    float netAmount;
    float tax;
    float totalAmount;
    List<OrderLine> orderLines = new ArrayList<OrderLine>();
    Status status = Status.OPEN;
    String trackingNumber;

    @Id @GeneratedValue
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

    @Transient
    public boolean isEmpty() {
        return (orderLines == null) || (orderLines.size()==0);
    }
    

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL)
    public List<OrderLine> getOrderLines() {
        return orderLines;
    }
    public void setOrderLines(List<OrderLine> lines) {
        this.orderLines = lines;
    }

    public void addProduct(Product product, int quantity) {
        for (OrderLine line: orderLines) {
            if (product.getProductId() == line.getProduct().getProductId()) {
                line.addQuantity(quantity);
                return;
            }
        }

        OrderLine line = new OrderLine();
        line.setProduct(product);
        line.setQuantity(quantity);
        line.setOrder(this);

        orderLines.add(line);
    }


    public void removeProduct(Product product) {
        for (OrderLine line: orderLines) {
            if (product.getProductId() == line.getProduct().getProductId()) { 
                orderLines.remove(line);
                return;
            }
        }
    }

    
    @ManyToOne
    @JoinColumn(name="USERID")
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

    @Column(name="TRACKING")
    public String getTrackingNumber() { 
       return trackingNumber;
    }
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Column(name="STATUS")
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    @Transient
    public int getStatusCode() {
        return status.ordinal();
    }

    public void calculateTotals() {
        float total = 0;
        
        int index = 1;
        for (OrderLine line: orderLines) {
            line.setPosition(index++);
            total +=  line.getProduct().getPrice() * line.getQuantity();
        }
        
        setNetAmount(total);
        setTax((float) (getNetAmount() * .0825));
        setTotalAmount(getNetAmount() + getTax());
    }


    public void cancel() {
        setStatus(Order.Status.CANCELLED);
    }

    public void process() {
        setStatus(Order.Status.PROCESSING);
    }

    public void ship(String tracking) {
        setStatus(Order.Status.SHIPPED);
        setTrackingNumber(tracking);
    }




}

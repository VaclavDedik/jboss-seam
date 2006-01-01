/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;
import java.util.*;

import javax.ejb.*;
import javax.persistence.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("cart")
@Scope(ScopeType.SESSION)
//@Intercept(InterceptionType.ALWAYS)
@Interceptor(SeamInterceptor.class)
public class ShoppingCartBean
    implements ShoppingCart,
               Serializable
{
    static final long serialVersionUID = 8722576722482084467L;

    @In(value="currentUser",required=false)
    Customer customer;

    @PersistenceContext(unitName="dvd",type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    Order cartOrder = new Order();
    //List<OrderLine> cart = new ArrayList<OrderLine>();
    Map<Product,Boolean> cartSelection  = new HashMap<Product,Boolean>();

    @Out(required=false)
    Order order = null;


    @Out(scope=ScopeType.BUSINESS_PROCESS, required=false)
    long orderId;
    @Out(scope=ScopeType.BUSINESS_PROCESS, required=false)
    float amount;
    @Out(value="customer",scope=ScopeType.BUSINESS_PROCESS, required=false)
    String customerName;

    public List<OrderLine> getCart() {
        //resetCartNumbers();
        return cartOrder.getOrderLines();
    }
    public boolean getIsEmpty() {
        return cartOrder.isEmpty();
    }

    public void addProduct(Product product, int quantity) {
        product = em.find(Product.class, product.getProductId());

        cartOrder.addProduct(product,quantity);
        cartOrder.calculateTotals();
    }


    public Map getCartSelection() {
        return cartSelection;
    }


    public float getSubtotal() {
        return cartOrder.getNetAmount();
    }

    public float getTax() {
        return cartOrder.getTax();
    }

    public float getTotal() {
        return cartOrder.getTotalAmount();
    }

    
    public String updateCart() {
        List<OrderLine> newLines =  new ArrayList<OrderLine>();

        for (OrderLine line: cartOrder.getOrderLines()) {
            if (line.getQuantity() > 0) {
                Boolean selected = (Boolean) cartSelection.get(line);
                if ((selected==null) || (selected.booleanValue()==false)) {
                    newLines.add(line);
                    //newCartSelect.put(line, Boolean.FALSE);
                }
            }
        }        
        cartOrder.setOrderLines(newLines);
        cartOrder.calculateTotals();

        cartSelection = new HashMap<Product,Boolean>();
         
        return null;
    }


    @CreateProcess(definition="OrderManagement")
    public String purchase() {
        try {
            order = purchase(customer, cartOrder);
            cartOrder = new Order();

            orderId  = order.getOrderId();
            amount   = order.getNetAmount();
            customerName = order.getCustomer().getUserName();

            return "complete";
        } catch (InsufficientQuantityException e) {
            for (Product product: e.getProducts()) {
                Utils.warnUser("checkoutInsufficientQuantity", 
                    new Object[] {product.getTitle()});
            }
            
            return null;
        }
    }


    
    private Order purchase(Customer customer, Order order) 
        throws InsufficientQuantityException
    {
        order.setCustomer(customer);
        order.setOrderDate(new Date());

        List<Product> errorProducts = new ArrayList<Product>();
        for (OrderLine line: order.getOrderLines()) {
            Inventory inv = line.getProduct().getInventory();
            if (!inv.order(line.getQuantity())) {
                errorProducts.add(line.getProduct());
            }
        }

        if (errorProducts.size()>0) {
            throw new InsufficientQuantityException(errorProducts);
        }

        order.calculateTotals();
        em.persist(order);

        return order;
    }

    @Destroy
    @Remove
    public void destroy() {}

}

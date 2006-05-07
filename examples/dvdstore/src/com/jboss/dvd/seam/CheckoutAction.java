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
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.*;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;


@Stateful
@Name("checkout")
    //@Conversational(ifNotBegunOutcome="customer")
public class CheckoutAction
    implements Checkout,
               Serializable
{
    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;

    @In(value="currentUser",required=false)
    Customer customer;

    @In(create=true)
    ShoppingCart cart;

    @Out(scope=ScopeType.CONVERSATION,required=false)
    Order currentOrder;
    @Out(scope=ScopeType.CONVERSATION,required=false)
    Order completedOrder;

    @Out(scope=ScopeType.BUSINESS_PROCESS, required=false)
    long orderId;
    @Out(scope=ScopeType.BUSINESS_PROCESS, required=false)
    float amount;
    @Out(value="customer",scope=ScopeType.BUSINESS_PROCESS, required=false)
    String customerName;

    @Begin(nested=true, pageflow="checkout") 
    public void startCheckout() {
    }

    public void createOrder() {
        currentOrder = new Order();

        for (OrderLine line: cart.getCart()) {
            currentOrder.addProduct(em.find(Product.class, line.getProduct().getProductId()),
                                    line.getQuantity());
        }

        currentOrder.calculateTotals();
        cart.resetCart();
    }

    @End
    @CreateProcess(definition="OrderManagement")
    public void submitOrder() {
        try {
            completedOrder = purchase(customer, currentOrder);

            orderId      = completedOrder.getOrderId();
            amount       = completedOrder.getNetAmount();
            customerName = "anonymous";
            //customerName = completedOrder.getCustomer().getUserName();

            //return "complete;"
        } catch (InsufficientQuantityException e) {
            for (Product product: e.getProducts()) {
                Contexts.getEventContext().set("prod", product);
                FacesMessages.instance().addFromResourceBundle("checkoutInsufficientQuantity");
            }
            
            //return null;
        } catch (Throwable t) {
            System.out.println("----------------------------");
            t.printStackTrace();
        }

        System.out.println("DDDDDDDDDDDDDDDDDDDDDOOOOOOONE!!!!!!!!!!");
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

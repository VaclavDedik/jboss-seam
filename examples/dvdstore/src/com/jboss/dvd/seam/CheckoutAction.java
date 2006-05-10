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

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;


@Stateful
@Name("checkout")
@Conversational(ifNotBegunOutcome="checkout")
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

//     public void startCheckout() {
//     }

    @Begin(nested=true, pageflow="checkout") 
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
            customerName = completedOrder.getCustomer().getUserName();

            //return "complete;"
        } catch (InsufficientQuantityException e) {
            for (Product product: e.getProducts()) {
                Contexts.getEventContext().set("prod", product);
                FacesMessages.instance().addFromResourceBundle("checkoutInsufficientQuantity");
            }
            
            //return null;
        } catch (Throwable t) {
            t.printStackTrace();
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

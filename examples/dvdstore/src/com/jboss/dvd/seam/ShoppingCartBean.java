/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import static org.jboss.seam.InterceptionType.ALWAYS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Stateful;
import javax.ejb.Remove;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("cart")
@Scope(ScopeType.SESSION)
@Intercept(ALWAYS)
@Interceptor(SeamInterceptor.class)
public class ShoppingCartBean
    implements ShoppingCart,
               Serializable
{
    @In("currentUser")
    Customer customer;

    @PersistenceContext(unitName="dvd")
    EntityManager em;

    List<SelectableItem<OrderLine>> cart = 
        new ArrayList<SelectableItem<OrderLine>>();

    @Out(required=false)
    Order order = null;

    public ShoppingCartBean() {
        // System.out.println("!!!!!!!!!!!!!!!!!!! CREATE CARTBEAN " + this);
    }

    public boolean getIsEmpty() {
        return cart.size() == 0;
    }

    public void addProduct(Product product, int quantity) {
        for (SelectableItem<OrderLine> item: cart) {
            if (product.getProductId() == item.getItem().getProduct().getProductId()) {
                item.getItem().addQuantity(quantity);
                return;
            }
        }

        OrderLine line = new OrderLine();
        line.setProduct(product);
        line.setQuantity(quantity);
        cart.add(new SelectableItem<OrderLine>(line));
    }

    public List<SelectableItem<OrderLine>> getCart() {
        resetCartNumbers();
        return cart;
    }

    public float getSubtotal() {
        float total = 0;
        
        for (SelectableItem<OrderLine> item: cart) {
            OrderLine line = item.getItem();
            total +=  line.getProduct().getPrice() * line.getQuantity();
        }
        
        return total;
    }

    public float getTax() {
        return (float) (getSubtotal() * .0825);
    }

    public float getTotal() {
        return getSubtotal() + getTax();
    }


    public String updateCart() {
        List<SelectableItem<OrderLine>> newCart = new ArrayList<SelectableItem<OrderLine>>();
        for(SelectableItem<OrderLine> item: cart) {
            if (!item.getSelected() && (item.getItem().getQuantity()>0)) {
                newCart.add(item);
            }
        }        
        cart = newCart;
        return null;
    }


    public void resetCartNumbers() {
        if (cart !=null) {
            int index=1;
            for(SelectableItem<OrderLine> item: cart) {
                item.getItem().setPosition(index++);
            }
        }
    }


    public String purchase() {
        List<OrderLine> lines = new ArrayList<OrderLine>();

        resetCartNumbers();
        for(SelectableItem<OrderLine> item: cart) {
            lines.add(item.getItem());
        }

        try {
            order = purchase(customer, lines);
            cart = new ArrayList<SelectableItem<OrderLine>>(); 
            return "complete";
        } catch (InsufficientQuantityException e) {
            for (Product product: e.getProducts()) {
                Utils.warnUser("checkoutInsufficientQuantity", new Object[] {product.getTitle()});
            }
            
            return null;
        }
    }

    
    private Order purchase(Customer customer, List<OrderLine> lines) 
        throws InsufficientQuantityException
    {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(new Date());

        List<Product> errorProducts = new ArrayList<Product>();
        float total = 0;
        for (OrderLine line: lines) {
            total += line.getQuantity() * line.getProduct().getPrice();
            line.setOrderDate(order.getOrderDate());
            line.setOrder(order); 

            Inventory inv = line.getProduct().getInventory();
            if (!inv.order(line.getQuantity())) {
                errorProducts.add(line.getProduct());
            }
        }

        if (errorProducts.size()>0) {
            throw new InsufficientQuantityException(errorProducts);
        }

        order.setOrderLines(lines);

        order.setNetAmount(total);
        order.setTax((float) (order.getNetAmount() * .0825));
        order.setTotalAmount(order.getNetAmount() + order.getTax());

        em.persist(order);

        return order;
    }

    @Destroy
    @Remove
    public void destroy() {
    }


}

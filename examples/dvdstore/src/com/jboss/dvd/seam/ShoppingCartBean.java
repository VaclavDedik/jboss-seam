/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import static org.jboss.seam.ScopeType.BUSINESS_PROCESS;
import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.*;

import org.jboss.seam.annotations.*;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;

@Stateful
@Name("cart")
@Scope(SESSION)
public class ShoppingCartBean
    implements ShoppingCart,
               Serializable
{
    static final long serialVersionUID = 8722576722482084467L;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    Order cartOrder = new Order();
    Map<Product,Boolean> cartSelection  = new HashMap<Product,Boolean>();

    public List<OrderLine> getCart() {
        return cartOrder.getOrderLines();
    }
    public boolean getIsEmpty() {
        return cartOrder.isEmpty();
    }

    public void addProduct(Product product, int quantity) {
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


    public void updateCart() {
        List<OrderLine> newLines =  new ArrayList<OrderLine>();

        for (OrderLine line: cartOrder.getOrderLines()) {
            if (line.getQuantity() > 0) {
                Boolean selected = cartSelection.get(line);
                if (selected==null || !selected) {
                    newLines.add(line);
                }
            }
        }        
        cartOrder.setOrderLines(newLines);
        cartOrder.calculateTotals();

        cartSelection = new HashMap<Product,Boolean>();
    }

    public void resetCart() {
        cartOrder = new Order();
    }

    @Destroy
    @Remove
    public void destroy() {}

}

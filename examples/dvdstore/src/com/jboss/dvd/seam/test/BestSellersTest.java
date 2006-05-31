package com.jboss.dvd.seam.test;

import java.util.*;
import javax.faces.model.*;
import org.testng.annotations.Test;

import com.jboss.dvd.seam.*;

import static org.testng.AssertJUnit.*;

public class BestSellersTest 
    extends BaseTest
{
    @Test
    public void testTopProducts() 
        throws Exception
    {
        
        String id =  new Script() {
            protected void renderResponse()
            {
                ListDataModel model = (ListDataModel) getInstance("topProducts");
                
                assertNotNull("topProducts", model);
                assertEquals("topProducts size",  8, model.getRowCount());

                List<Product> products = (List<Product>) model.getWrappedData();

                Product prev = null;
                for (Product p: products) {
                    if (prev != null) {
                        assertTrue("descending order", 
                                   p.getInventory().getSales() <= prev.getInventory().getSales());
                    }

                    prev = p;
                }

                // 14.98/29.99/39.95

                assertEquals("price 1", 14.98f, products.get(0).getPrice());
                assertEquals("price 2", 29.99f, products.get(1).getPrice());
                assertEquals("price 3", 39.95f, products.get(2).getPrice());
            }               
        }.run();
    }


    @Test
    public void testAddToCart() 
        throws Exception
    {
        String id = new Script() {
            protected void invokeApplication() {
                ListDataModel model = (ListDataModel) getInstance("topProducts");
                model.setRowIndex(1);

                System.out.println("**** PRODUCT" + ((Product)model.getRowData()).getProductId());

                BestSellers best = (BestSellers) getInstance("bestsellers");
                best.addToCart();
            }

            protected void renderResponse() {
                ShoppingCart cart = (ShoppingCart) getInstance("cart");

                assertNotNull("cart", cart);
                assertFalse("cart shouldn't be empty", 
                            cart.getIsEmpty());
                assertEquals("cart size", 1, cart.getCart().size());
                assertEquals("cart total", 29.99f, cart.getSubtotal());

            }
        }.run();

        id = new Script(id) {
            protected void invokeApplication() {
                ListDataModel model = (ListDataModel) getInstance("topProducts");
                model.setRowIndex(2);

                BestSellers best = (BestSellers) getInstance("bestsellers");
                best.addToCart();
            }

            protected void renderResponse() {
                ShoppingCart cart = (ShoppingCart) getInstance("cart");

                assertNotNull("cart", cart);
                assertFalse("cart shouldn't be empty", 
                            cart.getIsEmpty());
                assertEquals("cart size", 2, cart.getCart().size());
                assertEquals("cart total", 69.94f, cart.getSubtotal());
            }
        }.run();

        id = new Script(id) {
            protected void invokeApplication() {
                ListDataModel model = (ListDataModel) getInstance("topProducts");
                model.setRowIndex(1);

                BestSellers best = (BestSellers) getInstance("bestsellers");
                best.addToCart();
            }

            protected void renderResponse() {
                ShoppingCart cart = (ShoppingCart) getInstance("cart");

                assertNotNull("cart", cart);
                assertFalse("cart shouldn't be empty", 
                            cart.getIsEmpty());

                // still two items, but total increase
                assertEquals("cart size", 2, cart.getCart().size());
                assertEquals("cart total", 99.93f, cart.getSubtotal());
            }
        }.run();
        
    }

}

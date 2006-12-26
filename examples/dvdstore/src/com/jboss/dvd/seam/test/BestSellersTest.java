package com.jboss.dvd.seam.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import com.jboss.dvd.seam.BestSellers;
import com.jboss.dvd.seam.Product;
import com.jboss.dvd.seam.ShoppingCart;

public class BestSellersTest 
    extends SeamTest
{
    @Test
    public void testTopProducts() 
        throws Exception
    {
        
        new NonFacesRequest() {
            @Override
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

                assertEquals("price 1", new BigDecimal("14.98"), products.get(0).getPrice());
                assertEquals("price 2", new BigDecimal("29.99"), products.get(1).getPrice());
                assertEquals("price 3", new BigDecimal("39.95"), products.get(2).getPrice());
            }               
        }.run();
    }


    @Test
    public void testAddToCart() 
        throws Exception
    {
        String id = new FacesRequest() {
           @Override 
           protected void invokeApplication() {
                ListDataModel model = (ListDataModel) getInstance("topProducts");
                model.setRowIndex(1);

                BestSellers best = (BestSellers) getInstance("bestsellers");
                best.addToCart();
            }
            @Override
            protected void renderResponse() {
                ShoppingCart cart = (ShoppingCart) getInstance("cart");

                assertNotNull("cart", cart);
                assertFalse("cart shouldn't be empty", 
                            cart.getIsEmpty());
                assertEquals("cart size", 1, cart.getCart().size());
                assertEquals("cart total", new BigDecimal("29.99"), cart.getSubtotal());

            }
        }.run();

        id = new FacesRequest(null, id) {
           @Override
            protected void invokeApplication() {
                ListDataModel model = (ListDataModel) getInstance("topProducts");
                model.setRowIndex(2);

                BestSellers best = (BestSellers) getInstance("bestsellers");
                best.addToCart();
            }
           @Override
            protected void renderResponse() {
                ShoppingCart cart = (ShoppingCart) getInstance("cart");

                assertNotNull("cart", cart);
                assertFalse("cart shouldn't be empty", 
                            cart.getIsEmpty());
                assertEquals("cart size", 2, cart.getCart().size());
                assertEquals("cart total", new BigDecimal("69.94"), cart.getSubtotal());
            }
        }.run();

        id = new FacesRequest(null, id) {
           @Override
            protected void invokeApplication() {
                ListDataModel model = (ListDataModel) getInstance("topProducts");
                model.setRowIndex(1);

                BestSellers best = (BestSellers) getInstance("bestsellers");
                best.addToCart();
            }
           @Override
            protected void renderResponse() {
                ShoppingCart cart = (ShoppingCart) getInstance("cart");

                assertNotNull("cart", cart);
                assertFalse("cart shouldn't be empty", 
                            cart.getIsEmpty());

                // still two items, but total increase
                assertEquals("cart size", 2, cart.getCart().size());
                assertEquals("cart total", new BigDecimal("99.93"), cart.getSubtotal());
            }
        }.run();
        
    }

}

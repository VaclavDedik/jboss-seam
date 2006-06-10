package com.jboss.dvd.seam.test;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import com.jboss.dvd.seam.StoreManager;

public class StoreManagerTest 
    extends BaseTest
{
    @Test
    public void testTopProducts() 
        throws Exception
    {
        
        String id =  new Script() {
            StoreManager manager;

            protected void updateModelValues()
            {
                manager = (StoreManager) getInstance("stats");
            }
                

            protected void renderResponse()
            {
                // these are from order instances - 
                assertEquals("number orders",   0L,    manager.getNumberOrders());
                assertEquals("total sales",     0.0,   manager.getTotalSales());

                // these are from inventory
                assertEquals("units sold",      5734,  manager.getUnitsSold());
                assertEquals("total inventory", 23432, manager.getTotalInventory());
            }               
        }.run();
    }

}

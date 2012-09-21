package com.jboss.dvd.seam.test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jboss.dvd.seam.Accept;
import com.jboss.dvd.seam.Product;

@RunWith(Arquillian.class)
public class BestSellersTest 
    extends JUnitSeamTest
{
   
   @Deployment(name = "BestSellersTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      WebArchive web = ShrinkWrap.create(ZipImporter.class, "seam-dvdstore.war").importFrom(new File("target/seam-dvdstore.war")).as(WebArchive.class);
      web.addPackages(true, Accept.class.getPackage());

      return web;
   }
   
   
    @Test
    public void testTopProducts() 
        throws Exception
    {        
        new NonFacesRequest() {         
            @SuppressWarnings("unchecked")
            @Override
            protected void renderResponse()
            {
                List<Product> products = (List<Product>) getValue("#{topProducts}");

                Assert.assertNotNull("topProducts", products);
                Assert.assertEquals("topProducts size",  8, products.size());               

                Product prev = null;
                for (Product p: products) {
                    if (prev != null) {
                    	Assert.assertTrue("descending order", 
                                p.getInventory().getSales() <= prev.getInventory().getSales());
                    }

                    prev = p;
                }

                Assert.assertEquals("price 1", new BigDecimal("14.98"), products.get(0).getPrice());
                Assert.assertEquals("price 2", new BigDecimal("29.99"), products.get(1).getPrice());
                Assert.assertEquals("price 3", new BigDecimal("39.95"), products.get(2).getPrice());
            }               
        }.run();
    }
}

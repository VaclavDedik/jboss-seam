package com.jboss.dvd.seam.test;

import java.io.File;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jboss.dvd.seam.Accept;
import com.jboss.dvd.seam.Product;

@RunWith(Arquillian.class)
public class ProductUnitTest 
   extends JUnitSeamTest
{
   @Deployment(name = "ProductUnitTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      WebArchive web = ShrinkWrap.create(ZipImporter.class, "seam-dvdstore.war").importFrom(new File("target/seam-dvdstore.war")).as(WebArchive.class);
      web.addPackages(true, Accept.class.getPackage());

      return web;
   }
   
    @Ignore //AS7-4576
    @Test
    public void testRequiredAttributes()
        throws Exception
    {
        new ComponentTest() {

            @Override
            protected void testComponents()
                throws Exception 
            {
                Product p = new Product();

                EntityManager em = (EntityManager) getValue("#{entityManager}");
                
                try {
                   em.persist(p);
                   Assert.fail("empty product persisted");
                } catch (ConstraintViolationException e) {
                    // good
                }
            }
        }.run();
    }

     @Test 
     public void testCreateDelete() 
         throws Exception 
     {
         final Product p = new Product();
         p.setTitle("test");

         new FacesRequest() {
            protected void invokeApplication()
            {
                EntityManager em = (EntityManager) getValue("#{entityManager}");
                em.persist(p);
            }
            
           
         }.run();
         
         new FacesRequest() {
             protected void invokeApplication()
             { 
                 EntityManager em = (EntityManager) getValue("#{entityManager}");
                 Product found = em.find(Product.class ,p.getProductId());
                 Assert.assertNotNull("find by id", found);
                 Assert.assertEquals("id", p.getProductId(), found.getProductId());
                 Assert.assertEquals("title", "test", found.getTitle());
         
                 em.remove(found);
             }
          }.run();
         
          new FacesRequest() {
              protected void invokeApplication()
              { 
                  EntityManager em = (EntityManager) getValue("#{entityManager}");
                  Product found = em.find(Product.class ,p.getProductId());

                  Assert.assertNull("deleted product", found);
              }
           }.run();
          
 
     }
    
}

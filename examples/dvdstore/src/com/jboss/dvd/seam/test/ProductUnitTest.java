package com.jboss.dvd.seam.test;

import java.util.*;

import javax.persistence.*;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

import com.jboss.dvd.seam.*;

public class ProductUnitTest 
    extends BaseTest
{   
    EntityManager em() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dvdDatabase");
        EntityManager        em  = emf.createEntityManager();
        assertNotNull("entity manager", em);
        assertTrue("entity manager open", em.isOpen());
        return em;
    }


    @Test
    public void testRequiredAttributes()
        throws Exception
    {
        Product p = new Product();

        EntityManager em = em();
        try {
            em.persist(p);
            fail("empty product persisted");
        } catch (PersistenceException e) {
            // good
        } finally {
            em.close();
        }
    }

    @Test 
    public void testCreateDelete() {
        EntityManager em = em();

        Product p = new Product();
        p.setTitle("test");

        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();

        long id = p.getProductId();
        assertTrue("product id set", id != 0);
        
        p = em.find(Product.class ,id);
        assertNotNull("find by id", p);
        assertEquals("id", id, p.getProductId());
        assertEquals("title", "test", p.getTitle());

        em.getTransaction().begin();
        em.remove(p);
        em.getTransaction().commit();
        
        p = em.find(Product.class, id);
        assertNull("deleted product", p);
    }
    
}

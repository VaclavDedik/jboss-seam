package org.jboss.seam.example.spring;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("seamBookingDao")
public class SeamBookingDao {
    
    @In
    EntityManager em;
    
    @SuppressWarnings("unchecked")
    public List<Hotel> getHotels() {        
        return em.createQuery("select h from Hotel h").getResultList();
    }
    
}

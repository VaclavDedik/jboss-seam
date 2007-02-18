package org.jboss.seam.example.spring;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class SpringBookingDao {
    private JpaTemplate jpaTemplate;

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        jpaTemplate = new JpaTemplate(emf);
    }

    @SuppressWarnings("unchecked")
    public List<Hotel> getHotels() 
        throws DataAccessException 
    {
        return (List<Hotel>) jpaTemplate.execute(new JpaCallback() {        
            public Object doInJpa(EntityManager em) 
                throws PersistenceException 
            {
                return em.createQuery("select h from Hotel h").getResultList();
            }
        });
    }
}
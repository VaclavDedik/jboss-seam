package org.jboss.seam.example.spring;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.jboss.seam.jsf.ListDataModel;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class HotelSearchingAction
{
   private JpaTemplate jpaTemplate;
   
   private String searchString;
   private int pageSize = 10;
   private int page;
   
   private List<Hotel> hotels;
   
   public void setEntityManagerFactory(EntityManagerFactory emf) {
       jpaTemplate = new JpaTemplate(emf);
   }
   
   public List<Hotel> getHotels() {
       return hotels;
   }
   
   public DataModel getHotelsModel() {
       List data = getHotels();
       
       if (data == null) {
           data = new ArrayList(0);
       }
       
       return new ListDataModel(data);       
   }
   
   public void find()
   {
      page = 0;
      queryHotels();
   }
   public void nextPage()
   {
      page++;
      queryHotels();
   }
      
   @SuppressWarnings("unchecked")
   private void queryHotels()
   {       
       hotels = (List<Hotel>) jpaTemplate.execute(new JpaCallback() {        
           public Object doInJpa(EntityManager em) 
               throws PersistenceException 
           {
               String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
               return em.createQuery("select h from Hotel h where lower(h.name) like :search or lower(h.city) like :search or lower(h.zip) like :search or lower(h.address) like :search")
                     .setParameter("search", searchPattern)
                     .setMaxResults(pageSize)
                     .setFirstResult( page * pageSize )
                     .getResultList();              
           }
       });
       System.out.println("HOTELS:" + hotels);
   }
   
   public boolean isNextPageAvailable()
   {
      return hotels!=null && hotels.size()==pageSize;
   }
   
   public int getPageSize() {
      return pageSize;
   }
   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }
   public String getSearchString()
   {
      return searchString;
   }
   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }
}
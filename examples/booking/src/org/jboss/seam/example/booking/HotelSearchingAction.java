//$Id$
package org.jboss.seam.example.booking;

import static javax.persistence.PersistenceContextType.EXTENDED;

import java.util.List;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("hotelSearch")
@Scope(ScopeType.SESSION)
@Interceptors(SeamInterceptor.class)
@LoggedIn
public class HotelSearchingAction implements HotelSearching
{
   
   @PersistenceContext
   private EntityManager em;
   
   private String searchString;
   private int pageSize = 10;
   
   @DataModel
   private List<Hotel> hotels;
   
   public String find()
   {
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = em.createQuery("from Hotel where lower(name) like :search or lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(pageSize)
            .getResultList();
      
      return "main";
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
   
   @Destroy @Remove
   public void destroy() {}

}
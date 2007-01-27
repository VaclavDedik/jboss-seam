//$Id$
package org.jboss.seam.example.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import org.jboss.seam.annotations.In;

@Name("hotelSearch")
@Scope(ScopeType.SESSION)
public class HotelSearchingAction
{
   
   @In
   private EntityManager em;
   
   private String searchString;
   private int pageSize = 10;
   private int page;
   
   @DataModel
   private List<Hotel> hotels;
   
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
      
   private void queryHotels()
   {
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = em.createQuery("select h from Hotel h where lower(h.name) like :search or lower(h.city) like :search or lower(h.zip) like :search or lower(h.address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .getResultList();
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

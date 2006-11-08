//$Id$
package org.jboss.seam.example.hibernate;

import java.util.List;
import org.hibernate.Session;

import org.jboss.seam.annotations.In;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

@Name("hotelSearch")
@Scope(ScopeType.SESSION)
// @LoggedIn
public class HotelSearchingAction {
   
   @In (create=true)
   private Session bookingDatabase;
   
   private String searchString;
   private int pageSize = 10;
   private int page;
   
   @DataModel
   private List<Hotel> hotels;
   
   public String find()
   {
      page = 0;
      queryHotels();   
      return "main";
   }

   public String nextPage()
   {
      page++;
      queryHotels();
      return "main";
   }
      
   private void queryHotels()
   {
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = bookingDatabase.createQuery("select h from Hotel h where lower(h.name) like :search or lower(h.city) like :search or lower(h.zip) like :search or lower(h.address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .list();
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

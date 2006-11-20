//$Id: HotelSearchingAction.java,v 1.1 2006/11/20 05:19:01 gavin Exp $
package org.jboss.seam.example.booking;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

@Stateful
@Name("hotelSearch")
@Scope(ScopeType.SESSION)
@LoggedIn
public class HotelSearchingAction implements HotelSearching
{
   
   @PersistenceContext
   private EntityManager em;
   
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
      hotels = em.createQuery("select h from Hotel h where lower(h.name) like :search or lower(h.city) like :search or lower(h.zip) like :search or lower(h.address) like :search")
            .setParameter( "search", getSearchPattern() )
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .getResultList();
   }

   private String getSearchPattern()
   {
      return searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
   }
   
   public boolean isNextPageAvailable()
   {
      return hotels!=null && hotels.size()==pageSize;
   }
   
   public SelectItem[] getPageSizes() {
      return new SelectItem[] { 
            new SelectItem(5, "5"), 
            new SelectItem(10, "10"), 
            new SelectItem(20, "20") 
         };
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

   public void handleSearchStringChange(ValueChangeEvent e) {
      page = 0;
      searchString = (String) e.getNewValue();
      queryHotels();
   }
   
   public List<SelectItem> getCities() {
      return em.createQuery("select distinct new javax.faces.model.SelectItem(h.city) from Hotel h where lower(h.city) like :search order by h.city")
            .setParameter("search", getSearchPattern())
            .getResultList();
   }
   
   public void handlePageSizeChange(ValueChangeEvent e)  {
      pageSize = ( (Long) e.getNewValue() ).intValue();
      queryHotels();
   }
   
   @Destroy @Remove
   public void destroy() {}

}

//$Id$
package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("findHotels")
@LocalBinding(jndiBinding="findHotels")
@Interceptor(SeamInterceptor.class)
@LoggedIn
public class FindHotelsAction implements FindHotels, Serializable
{
   private static final Logger log = Logger.getLogger(FindHotels.class);
   
   @PersistenceContext
   private EntityManager em;
   
   private String searchString;
   private List<Hotel> hotels;
   
   @Out(required=false)
   private Hotel hotel;
   
   private DataModel listDataModel = new ListDataModel();
   int rowIndex = 0;
   
   public DataModel getHotelsDataModel() {
      return listDataModel;
   }

   @Begin
   public String find()
   {
      hotel = null;
      hotels = em.createQuery("from Hotel where lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", '%' + searchString.toLowerCase().replace('*', '%') + '%')
            .setMaxResults(50)
            .getResultList();
      
      log.info(hotels.size() + " hotels found");
      
      listDataModel.setWrappedData(hotels);
      
      return "success";
   }
   
   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString==null ? 
            "*" : searchString;
   }
   
   public String selectHotel()
   {
      rowIndex = listDataModel.getRowIndex();
      setHotel();
      return "selected";
   }

   public String nextHotel()
   {
      if ( rowIndex<hotels.size()-1 )
      {
         listDataModel.setRowIndex(++rowIndex);
         setHotel();
      }
      return "redisplay";
   }

   public String lastHotel()
   {
      if (rowIndex>0)
      {
         listDataModel.setRowIndex(--rowIndex);
         setHotel();
      }
      return "redisplay";
   }

   private void setHotel()
   {
      hotel = (Hotel) listDataModel.getRowData();
      log.info( rowIndex + "=>" + hotel );
   }
      
   @Destroy @Remove
   public void destroy() {}
}

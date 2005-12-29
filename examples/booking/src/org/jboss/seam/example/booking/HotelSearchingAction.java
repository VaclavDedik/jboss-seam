//$Id$
package org.jboss.seam.example.booking;

import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("hotelSearching")
@Interceptor(SeamInterceptor.class)
@Conversational(ifNotBegunOutcome="main")
@LoggedIn
public class HotelSearchingAction implements HotelSearching, Serializable
{
   private static final Logger log = Logger.getLogger(HotelSearching.class);
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   private String searchString;
   
   @DataModel
   private List<Hotel> hotels;
   @DataModelSelectionIndex
   private int hotelIndex;
   
   @Out(required=false)
   private Hotel hotel;
   
   @In(create=true)
   private transient Conversation conversation;
   
   @Begin(join=true)
   public String find()
   {
      hotel = null;
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = em.createQuery("from Hotel where lower(name) like :search or lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(50)
            .getResultList();
      
      log.info(hotels.size() + " hotels found");
      
      return conversation.switchableOutcome("main", "Search hotels: " + searchString);
   }
   
   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }

   public String selectHotel()
   {
      if ( hotels==null ) return "main";
      setHotel();
      return "selected";
   }

   public String nextHotel()
   {
      if ( hotelIndex<hotels.size()-1 )
      {
         ++hotelIndex;
         setHotel();
      }
      return null;
   }

   public String lastHotel()
   {
      if (hotelIndex>0)
      {
         --hotelIndex;
         setHotel();
      }
      return null;
   }

   private void setHotel()
   {
      log.info( "hotel selected: " + hotelIndex + "=>" + hotel );
      hotel = hotels.get(hotelIndex);
   }
   
   @End
   public String clear()
   {
      hotels = null;
      hotel = null;
      return "main";
   }
   
   @Destroy @Remove
   public void destroy() {
      log.info("destroyed");
   }

}
//$Id$
package org.jboss.seam.example.crud;

import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("hotelOperations")
@Interceptor(SeamInterceptor.class)
public class HotelOperationsActions implements HotelOperations, Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;

   private static final Logger log = Logger.getLogger(HotelOperations.class);
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   @DataModel
   private List<Hotel> hotels;
   
   @DataModelSelectionIndex
   private int hotelIndex;
   
   @In(required=false) 
   @Out(required=false)
   private Hotel hotel;

   @In
   private transient FacesContext facesContext;

   @Factory("hotels")
   public void find()
   {
      hotels = em.createQuery("from Hotel h order by h.name")
            .getResultList();
      
      log.info(hotels.size() + " hotels found");  
   }

   public String createHotel()
   {
      
      return "create";
   }

   public String storeHotel()
   {
      em.persist(hotel);
      find();
      return "main";
   }

   public String deleteHotel()
   {
      em.remove((Hotel)hotels.get(hotelIndex));
      find();
      return "main";
   }

   public String listHotels()
   {
      return "main";
   }

   public String done()
   {
      return "home";
   }
   
   @Destroy @Remove
   public void destroy() {
      log.info("destroyed");
   }

   public List<Hotel> getHotels()
   {
      return hotels;
   }

   public int getHotelIndex()
   {
      return hotelIndex;
   }

}

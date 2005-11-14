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
@Name("hotelEditOperations")
@Interceptor(SeamInterceptor.class)
@Conversational(ifNotBegunOutcome = "home")
public class HotelEditOperationsActions implements HotelEditOperations, Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;

   private static final Logger log = Logger.getLogger(HotelOperations.class);

   @PersistenceContext(type = EXTENDED)
   private EntityManager em;

   @In
   private HotelOperations hotelOperations;

   @DataModel
   private List<Hotel> hotels;

   @DataModelSelectionIndex
   private int hotelIndex;

   @In(required = false)
   @Out(required = false)
   private Hotel hotel;

   @Begin
   public String editHotel()
   {
      log.info("Editing hotel: " + ((Hotel) hotelOperations.getHotels().get(hotelIndex)).getName());
      hotel = (Hotel) em.createQuery("from Hotel h where h.id = :index").setParameter("index",
            ((Hotel) hotelOperations.getHotels().get(hotelOperations.getHotelIndex())).getId()).getSingleResult();
      return "edit";
   }

   @End
   public String updateHotel()
   {
      log.info("Updating hotel: " + hotel.getName());
      return "home";
   }

   @Destroy
   @Remove
   public void destroy()
   {
      log.info("destroyed");
   }

}

//$Id: BookingListAction.java 8748 2008-08-20 12:08:30Z pete.muir@jboss.org $
package org.jboss.seam.example.jpa;

//import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

@Scope(SESSION)
@Name("bookingList")
//@TransactionAttribute(REQUIRES_NEW)
public class BookingListAction implements Serializable
{
   
   @In
   private EntityManager em;
   
   @In
   private User user;
   
   @DataModel
   private List<Booking> bookings;
   @DataModelSelection 
   private Booking booking;
   
   @Logger 
   private Log log;
   
   @Factory
   @Observer("bookingConfirmed")
// see JBSEAM-4928
//   @Transactional
   public void getBookings()
   {
// JPA 1.0 way      
//    bookings = em.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
//          .setParameter("username", user.getUsername())
//          .getResultList();      

      //JPA 2.0 way - using new CriteriaBuilder API for dynamic query execution
      //this easily check the typos in comparison to usage of string query
      CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
      CriteriaQuery<Booking> query = criteriaBuilder.createQuery(Booking.class);
      Root<Booking> hotelBooking = query.from(Booking.class);
      query.where(criteriaBuilder.equal(hotelBooking.get("user"), user));
      query.orderBy(criteriaBuilder.asc(hotelBooking.get("checkinDate")));
      
      bookings = em.createQuery(query).getResultList();
   }
   
   public void cancel()
   {
      log.info("Cancel booking: #{bookingList.booking.id} for #{user.username}");
      Booking cancelled = em.find(Booking.class, booking.getId());
      if (cancelled!=null) em.remove( cancelled );
      getBookings();
      FacesMessages.instance().add("Booking cancelled for confirmation number #0", booking.getId());
   }
   
   public Booking getBooking()
   {
      return booking;
   }
   
}

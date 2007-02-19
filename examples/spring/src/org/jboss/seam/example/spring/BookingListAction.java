//$Id$
package org.jboss.seam.example.spring;

// import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

// import javax.ejb.TransactionAttribute;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.log.Log;

import org.hibernate.Session;

@Scope(SESSION)
@Name("bookingList")
// @TransactionAttribute(REQUIRES_NEW)
public class BookingListAction implements Serializable
{

   @In("#{bookingService}")
   private BookingService bookingService;

   @In
   private User user;

   @DataModel
   private List<Booking> bookings;
   @DataModelSelection
   private Booking booking;

   @Logger
   private Log log;

   @Factory(value="")
   // @Observer("bookingConfirmed")
   public void getBookings()
   {
      bookings = bookingService.findBookingsByUsername(user.getUsername());
   }

   public void cancel()
   {
      log.info("Cancel booking: #{bookingList.booking.id} for #{user.username}");
      bookingService.cancelBooking(booking.getId());
      getBookings();
      FacesMessages.instance().add("Booking cancelled for confirmation number #{bookingList.booking.id}");
   }

   public Booking getBooking()
   {
      return booking;
   }

}

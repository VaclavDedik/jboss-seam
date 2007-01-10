//$Id$
package org.jboss.seam.example.hibernate;

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
   
   @In (create=true)
   private Session bookingDatabase;
   
   @In
   private User user;
   
   @DataModel
   private List<Booking> bookings;
   @DataModelSelection 
   private Booking booking;
   
   @Logger 
   private Log log;
   
   @Factory
   // @Observer("bookingConfirmed")
   public void getBookings()
   {
      bookings = bookingDatabase.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
            .setParameter("username", user.getUsername())
            .list();
   }
   
   public void cancel()
   {
      log.info("Cancel booking: #{bookingList.booking.id} for #{user.username}");
      Booking cancelled = (Booking) bookingDatabase.get(Booking.class, booking.getId());
      if (cancelled!=null) bookingDatabase.delete( cancelled );
      getBookings();
      FacesMessages.instance().add("Booking cancelled for confirmation number #{bookingList.booking.id}");
   }
   
   public Booking getBooking()
   {
      return booking;
   }
   
}

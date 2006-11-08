//$Id$
package org.jboss.seam.example.hibernate;

import org.hibernate.Session;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.log.Log;

@Scope(SESSION)
@Name("bookingList")
// @LoggedIn
public class BookingListAction implements Serializable
{
   
   @In (create=true)
   private Session bookingDatabase;
   
   @In
   private User user;
   
   @DataModel
   private List<Booking> bookings;
   @DataModelSelection 
   @Out(required=false)
   private Booking booking;
   
   @Logger 
   private Log log;
   
   @Factory
   @Observer("bookingConfirmed")
   public void getBookings()
   {
      bookings = bookingDatabase.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
            .setParameter("username", user.getUsername())
            .list();
   }
   
   public String cancel()
   {
      log.info("Cancel booking: #0 for #{user.username}", booking.getId());
      Booking cancelled = (Booking) bookingDatabase.get(Booking.class, booking.getId());
      if (cancelled!=null) bookingDatabase.delete( cancelled );
      getBookings();
      FacesMessages.instance().add("Booking cancelled for confirmation number #{booking.id}");
      return "main";
   }
   
   
}

//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

@Scope(SESSION)
@Name("bookingList")
public class BookingListAction implements Serializable
{
   @In
   private Session bookingDatabase;
   
   @In
   private User user;
   
   @DataModel
   private List<Booking> bookings;
   @DataModelSelection 
   private Booking booking;
   
   @Factory("bookings")
   public void find()
   {
      bookings = bookingDatabase.createQuery("from Booking b where b.user = :user order by b.checkinDate")
            .setParameter("user", user)
            .list();
   }
   
   public String cancel()
   {
      Booking cancelled = (Booking) bookingDatabase.get(Booking.class, booking.getId());
      if (cancelled!=null) bookingDatabase.delete( cancelled );
      refresh();
      return "cancelled";
   }
   
   public void refresh()
   {
      booking = null;
      find();
   }
      
}

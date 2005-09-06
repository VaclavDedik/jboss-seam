//$Id$
package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("bookingList")
@LocalBinding(jndiBinding="bookingList")
@Interceptor(SeamInterceptor.class)
@LoggedIn
public class BookingListAction implements BookingList, Serializable
{
   private static final Logger log = Logger.getLogger(BookingList.class);
   
   @In(create=true)
   private EntityManager bookingDatabase;
   
   @In
   private User user;
   
   @Out
   private DataModel bookingsDataModel = new ListDataModel();
   
   public String find()
   {
      List bookings = bookingDatabase.createQuery("from Booking b where b.user.username = :username order by b.checkinDate")
            .setParameter("username", user.getUsername())
            .getResultList();
      
      log.info(bookings.size() + " bookings found");
      
      bookingsDataModel.setWrappedData(bookings);
      
      return "bookings";
   }
   
}

//$Id$
package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name("bookingList")
@Interceptor(SeamInterceptor.class)
@LoggedIn
public class BookingListAction implements BookingList, Serializable
{
   private static final Logger log = Logger.getLogger(BookingList.class);
   
   @PersistenceContext
   private EntityManager em;
   
   @In
   private User user;
   
   @Out
   private List<Booking> bookings;
   
   public String find()
   {
      bookings = em.createQuery("from Booking b where b.user.username = :username order by b.checkinDate")
            .setParameter("username", user.getUsername())
            .getResultList();
      
      log.info(bookings.size() + " bookings found");
      
      return "bookings";
   }
   
}

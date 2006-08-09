//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface HotelBooking
{
   public String selectHotel(Hotel selectedHotel);
   
   public String bookHotel(User user);
   
   public String setBookingDetails();
   
   public String confirm(User user);
   public String cancel();
   
   public void destroy();
   
}
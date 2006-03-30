//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface HotelBooking
{
   public String selectHotel();
   
   public String bookHotel();
   
   public String setBookingDetails();
   
   public String confirm();
   public String cancel();
   
   public void destroy();
   
}
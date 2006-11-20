//$Id: HotelBooking.java,v 1.11 2006/09/28 01:16:05 gavin Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface HotelBooking
{
   public String selectHotel(Hotel selectedHotel);
   
   public String bookHotel();
   
   public String setBookingDetails();
   
   public String confirm();
   public String cancel();
   
   public void destroy();
   
}
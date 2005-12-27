//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Remote;

@Remote
public interface HotelBooking
{
   public String bookHotel();
   
   public String setBookingDetails();
   
   public String confirm();
   
   public String cancel();
   
   public void destroy();
   
}
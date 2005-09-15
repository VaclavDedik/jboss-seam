//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface HotelBooking
{
   public String getSearchString();
   public void setSearchString(String searchString);
   public String find();

   public Long getHotelId();
   public void setHotelId(Long hotelId);
   public String selectHotel();
   
   public String nextHotel();
   public String lastHotel();
   
   public String bookHotel();
   
   public String setBookingDetails();
   
   public String confirm();
   
   public void destroy();
   
}
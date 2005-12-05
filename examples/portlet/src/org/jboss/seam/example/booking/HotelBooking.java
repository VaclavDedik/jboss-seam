//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Remote;

@Remote
public interface HotelBooking
{
   public String getSearchString();
   public void setSearchString(String searchString);
   public String find();

   public String selectHotel();
   
   public String nextHotel();
   public String lastHotel();
   
   public String bookHotel();
   
   public String setBookingDetails();
   
   public String confirm();
   
   public void destroy();
   
}
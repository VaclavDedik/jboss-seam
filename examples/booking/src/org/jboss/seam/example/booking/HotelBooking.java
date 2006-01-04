//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Remote;

@Remote
public interface HotelBooking
{
   public int getPageSize();
   public void setPageSize(int pageSize);
   
   public String getSearchString();
   public void setSearchString(String searchString);
   public String find();

   public String selectHotel();
   
   public String nextHotel();
   public String lastHotel();
   
   public String clear();

   public String bookHotel();
   
   public String setBookingDetails();
   
   public String confirm();
   public String revise();
   
   public String searchResults();
   
   public void destroy();
   
}
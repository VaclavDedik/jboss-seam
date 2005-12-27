//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Remote;

@Remote
public interface HotelSearching
{
   public String getSearchString();
   public void setSearchString(String searchString);
   public String find();

   public String selectHotel();
   
   public String nextHotel();
   public String lastHotel();
   
   public String clear();
   
   public void destroy();
   
}
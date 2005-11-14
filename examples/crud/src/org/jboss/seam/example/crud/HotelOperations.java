//$Id$
package org.jboss.seam.example.crud;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface HotelOperations
{
   public String createHotel();
   public String storeHotel();
//   public String editHotel();
//   public String updateHotel();
   public String deleteHotel();
   public String listHotels();
   public String done();
   public void find();
   public void destroy();
   public List<Hotel> getHotels();
   public int getHotelIndex();
}
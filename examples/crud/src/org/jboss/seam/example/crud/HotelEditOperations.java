//$Id$
package org.jboss.seam.example.crud;

import javax.ejb.Remote;

@Remote
public interface HotelEditOperations
{
   public String editHotel();
   public String updateHotel();
   public void destroy();
}
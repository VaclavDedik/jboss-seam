//$Id: BookingList.java,v 1.3 2006/04/24 03:50:04 gavin Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface BookingList
{
   public void getBookings();
   public String cancel();
   public void destroy();
}
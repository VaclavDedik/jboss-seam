//$Id: BookingList.java,v 1.2 2006/11/20 18:03:01 gavin Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface BookingList
{
   public void getBookings();
   public String cancel();
   public void destroy();
}
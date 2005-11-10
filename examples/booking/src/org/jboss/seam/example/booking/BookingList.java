//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface BookingList
{
   public void find();
   public String cancel();
   public void refresh();
   public void destroy();
}
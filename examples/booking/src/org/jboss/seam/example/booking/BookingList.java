//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface BookingList
{
   public String find();  
}
//$Id: BookingList.java,v 1.1 2006/11/20 05:19:01 gavin Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;

import com.icesoft.faces.context.effects.Effect;

@Local
public interface BookingList
{
   public void getBookings();
   public String cancel();
   public Effect getEffect();
   public void destroy();
}
//$Id$
package org.jboss.seam.example.booking;

import java.util.List;

import javax.ejb.Local;

@Local
public interface FindHotels
{
   public String getSearchString();
   public void setSearchString(String searchString);
   public String find();
   public List getHotels();

   public int getSelectedHotel();
   public void setSelectedHotel(int i);
   
   public void destroy();

}
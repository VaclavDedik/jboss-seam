//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;
import javax.faces.model.DataModel;

@Local
public interface FindHotels
{
   public String getSearchString();
   public void setSearchString(String searchString);
   public String find();

   public DataModel getHotelsDataModel();
   public String selectHotel();
   
   public String nextHotel();
   public String lastHotel();
   
   public void destroy();
   
}
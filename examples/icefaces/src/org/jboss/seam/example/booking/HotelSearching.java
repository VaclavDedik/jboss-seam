//$Id: HotelSearching.java,v 1.7 2006/10/04 19:19:13 gavin Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;
import javax.faces.event.ValueChangeEvent;

@Local
public interface HotelSearching
{
   public int getPageSize();
   public void setPageSize(int pageSize);
   
   public String getSearchString();
   public void setSearchString(String searchString);
   public void handleSearchStringChange(ValueChangeEvent e);
   
   public String find();
   public String nextPage();
   public boolean isNextPageAvailable();

   public void destroy();
   
}

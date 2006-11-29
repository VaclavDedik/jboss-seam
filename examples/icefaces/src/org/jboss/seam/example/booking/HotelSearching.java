//$Id: HotelSearching.java,v 1.3 2006/11/20 16:54:53 gavin Exp $
package org.jboss.seam.example.booking;

import java.util.List;

import javax.ejb.Local;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

@Local
public interface HotelSearching
{
   public int getPageSize();
   public void setPageSize(int pageSize);
   
   public String getSearchString();
   public void setSearchString(String searchString);
   public void handleSearchStringChange(ValueChangeEvent e);
   public void handlePageSizeChange(ValueChangeEvent e);
   public SelectItem[] getPageSizes();
   public List<SelectItem> getCities();
   
   public String find();
   public String nextPage();
   public boolean isNextPageAvailable();

   public void destroy();
   
}

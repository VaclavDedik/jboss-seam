//$Id$
package org.jboss.seam.example.booking;

import java.util.List;

import javax.ejb.Local;

@Local
public interface FindHotels
{
   public String find();
   public List getHotels();
   public String getSearchString();
   public void setSearchString(String searchString);
   public void destroy();

}
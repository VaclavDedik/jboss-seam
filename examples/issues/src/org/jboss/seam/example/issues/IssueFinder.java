package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import javax.ejb.Local;


@Local
public interface IssueFinder {

   public String findFirstPage();
   public String findNextPage();
   public String findPreviousPage();
   
   public boolean isNextPage();
   public boolean isPreviousPage();
   
   public void refresh();
   
   public int getPageSize();
   public void setPageSize(int size);
   
   public String clear();
   
   public Issue getSelection();
   
   public void destroy();
   
   public Issue getExample();
   
   public String reorder();
    
}
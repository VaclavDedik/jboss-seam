package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import javax.ejb.Local;

@Local
public interface IssueEditor {
   public boolean isNew();
   
   public void initialize();
   
   public Issue getInstance();

   public String update();
   public String delete();
   public String create();
   
   public Comment getSelectedComment();
   
   public String select();
   
   public String createIssue();
   public String selectIssue();
   
   public void setDeveloper(String developer);
   public String getDeveloper();
   public String assignDeveloper();
   public String unassignDeveloper();
   
   public String resolve();
   public boolean isOpen();
   
   public String done();
   
   public String getDescription();
   
   public void destroy();
}

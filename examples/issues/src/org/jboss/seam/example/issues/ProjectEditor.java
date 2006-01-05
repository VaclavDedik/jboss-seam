package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import java.util.List;

import javax.ejb.Local;



@Local
public interface ProjectEditor {
   public boolean isNew();
   
   public Project getInstance();
   public void setInstance(Project instance);

   public String update();
   public String delete();
   public String create();

   public Issue getSelectedIssue();
   
   public String done();
   
   public String addDeveloper();
   public String getDeveloper();
   public void setDeveloper(String developer);
   public List<User> getDevelopers();
   
   public String getDescription();
   
   public void destroy();
}

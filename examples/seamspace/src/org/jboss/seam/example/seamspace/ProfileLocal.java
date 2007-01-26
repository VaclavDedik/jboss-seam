package org.jboss.seam.example.seamspace;

import java.util.List;

import javax.ejb.Local;

@Local
public interface ProfileLocal
{
  void display();
  
  void newMembers();
  
  List getLatestBlogs();
  void getMemberBlogs();
  
  void destroy();  
}

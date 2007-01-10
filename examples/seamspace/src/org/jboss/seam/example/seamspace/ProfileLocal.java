package org.jboss.seam.example.seamspace;

import javax.ejb.Local;

@Local
public interface ProfileLocal
{
  void display();
  void destroy();
  void newMembers();
}

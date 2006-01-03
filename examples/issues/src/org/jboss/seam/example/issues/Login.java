package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import javax.ejb.Local;

@Local
public interface Login {
   public User getInstance();
   public String login();
}

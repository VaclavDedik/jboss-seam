package org.jboss.seam.example.bpm;

import javax.ejb.Local;

@Local
public interface Login
{
   public String login();
}

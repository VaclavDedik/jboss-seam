package org.jboss.seam.example.bpm;

import javax.ejb.Local;

@Local
public interface Logout
{
   public String logout();
}
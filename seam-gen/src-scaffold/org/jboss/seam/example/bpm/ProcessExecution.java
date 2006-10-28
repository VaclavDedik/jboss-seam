package org.jboss.seam.example.bpm;

import javax.ejb.Local;

@Local
public interface ProcessExecution {  
   public String submitOrder();   
}
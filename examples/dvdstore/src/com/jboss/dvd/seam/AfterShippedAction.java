package com.jboss.dvd.seam;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * An example of a Seam component used to handle a
 * jBPM transition event.
 * 
 * @author Gavin King
 */
@Name("afterShippedAction")
public class AfterShippedAction {
    @In Long orderId;
    
    public void log()
    {
        System.out.println("We shipped: " + orderId);
    }
}

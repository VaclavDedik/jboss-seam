package com.jboss.dvd.seam;

import org.jboss.seam.annotations.Name;

/**
 * An example of a Seam component used to do jBPM
 * assignments. (This is silly, for such a simple
 * case, we would not need a component.)
 * 
 * @author Gavin King
 */
@Name("shipperAssignment")
public class ShipperAssignment
{
    public String getPooledActor()
    {
       return "shipper";
    }
}

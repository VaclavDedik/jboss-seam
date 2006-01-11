package com.jboss.dvd.seam;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("orderApproval")
public class OrderApprovalDecision {
   @In float amount;
   public String getHowLargeIsOrder()
   {
      return amount > 100 ? "large order" : "small order";
   }
}

package com.jboss.dvd.seam;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("approvalRequired")
public class ApprovalRequiredDecision {
   @In Float amount;
   public String orderType()
   {
      return amount > 100 ? "large order" : "small order";
   }
}

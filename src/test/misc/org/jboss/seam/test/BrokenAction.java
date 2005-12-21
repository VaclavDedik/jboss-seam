package org.jboss.seam.test;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("brokenAction")
public class BrokenAction {
   
   @In String name;
   
   public String go() {
      return "success";
   }

}

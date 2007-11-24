package org.jboss.seam.test.integration.bpm;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.bpm.CreateProcess;

@Name("seamExpressionEvaluatorTestController")
public class SeamExpressionEvaluatorTestController {
       
   private String name = "foo";
   
   
   @CreateProcess(definition="TestProcess2") 
   public void createProcess() 
   {            
   }
   
   public void logTrue()
   {
      System.out.println("true");
   }
   
   public String getName()
   {
      return this.name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
    
    
}
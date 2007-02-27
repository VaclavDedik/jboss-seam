package org.jboss.seam.example.takeaway;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.log.Log;

@Name("takeaway")
public class Takeaway
{
   
   @Logger
   private Log log;
   
   public void sendType() {
      log.info("Sending out types of food");
      Renderer.instance().render("/mail/type.xhtml");
   }
   
   @CreateProcess(definition="takeaway")
   public void createProcess() {
      log.info("Starting takeaway");
   }

}

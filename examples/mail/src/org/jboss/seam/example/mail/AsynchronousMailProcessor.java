package org.jboss.seam.example.mail;

import org.jboss.seam.annotations.Asynchronous;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.timer.Duration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Renderer;

@Name("asynchronousMailProcessor")
@AutoCreate
public class AsynchronousMailProcessor
{
   @Asynchronous
   public void scheduleSend(@Duration long delay, Person person) {
      try {
         Contexts.getEventContext().set("person", person);
         Renderer.instance().render("/simple.xhtml");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

package org.jboss.seam.example.mail;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Renderer;

@Name("rendererExample")
public class RendererExample
{
   
   @In(create=true)
   private Renderer renderer;
   
   @In(create=true)
   private FacesMessages facesMessages;
   
   public void send() {
     renderer.render("/rendererExample.xhtml");
     facesMessages.add("Email sent successfully");
   }

}

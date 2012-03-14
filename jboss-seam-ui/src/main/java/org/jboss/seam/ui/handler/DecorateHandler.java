package org.jboss.seam.ui.handler;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;

public class DecorateHandler extends ComponentHandler
{
   private com.sun.faces.facelets.tag.ui.DecorateHandler delegate;

   public DecorateHandler(ComponentConfig config)
   {
      super(config);
      if ( tag.getAttributes().get("template")!=null )
      {
         delegate = new com.sun.faces.facelets.tag.ui.DecorateHandler(config);
      }
   }
   
   @Override
   public void applyNextHandler(FaceletContext context, UIComponent component) 
      throws IOException, FacesException, ELException
   {
      if ( tag.getAttributes().get("template")!=null )
      {
         delegate.apply(context, component);
      }
      else
      {
         nextHandler.apply(context, component);
      }
   }

}

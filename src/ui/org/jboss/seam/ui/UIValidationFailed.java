package org.jboss.seam.ui;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;


public class UIValidationFailed extends javax.faces.component.UICommand
{
   
   @Override
   public String getRendererType()
   {
      return null;
   }
   
   @Override
   public void processValidators(FacesContext context)
   {
      super.processValidators(context);
      new AfterValidationEvent(this).queue();
   }
   
   @Override
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      super.broadcast(event);
      if (event instanceof AfterValidationEvent)
      {
         if ( FacesContext.getCurrentInstance().getRenderResponse() )
         {
            //validation failed
            getAction().invoke( FacesContext.getCurrentInstance(), null );
         }
      }
   }
   
}

package org.jboss.seam.ui;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;


public class AfterValidationEvent extends FacesEvent
{
   public AfterValidationEvent(UIComponent component)
   {
      super(component);
   }

   @Override
   public boolean isAppropriateListener(FacesListener listener)
   {
      return false;
   }

   @Override
   public void processListener(FacesListener listener)
   {
      throw new UnsupportedOperationException();
   }
}
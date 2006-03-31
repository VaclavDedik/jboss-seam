package org.jboss.seam.ui;

import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class HtmlActionLink extends HtmlOutputLink
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlActionLink";
   
   private UIAction uiAction;
   
   public HtmlActionLink()
   {
      uiAction = new UIAction();
      getChildren().add(uiAction);
      getChildren().add( new UIConversationId() );
   }

   @Override
   public void setValueBinding(String name, ValueBinding binding)
   {
      if ("action".equals(name) )
      {
         uiAction.setValueBinding(name, binding);
      }
      else
      {
         super.setValueBinding(name, binding);
      }
   }
   
   @Override
   public void restoreState(FacesContext context, Object state) {
      super.restoreState(context, state);
      uiAction = (UIAction) getChildren().get(0);
   }

   @Override
   public void processRestoreState(FacesContext context, Object state)
   {
      getChildren().remove(0);
      getChildren().remove(0);
      super.processRestoreState(context, state);
   }

}

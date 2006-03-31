package org.jboss.seam.ui;

import javax.faces.component.html.HtmlOutputLink;
import javax.faces.el.ValueBinding;

public class HtmlActionLink extends HtmlOutputLink
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlActionLink";

   private UIConversationPropagation uiConversationPropagation; 

   @Override
   public void setValueBinding(String name, ValueBinding binding)
   {
      if ( "action".equals(name) )
      {
         UIAction uiAction = new UIAction();
         getChildren().add(uiAction);
         uiAction.setValueBinding(name, binding);
      }
      else
      {
         super.setValueBinding(name, binding);
      }
   }
   
   private void initUiConversationPropagation()
   {
      if (uiConversationPropagation==null)
      {
         uiConversationPropagation = new UIConversationPropagation();
         getChildren().add(uiConversationPropagation);
      }
   }
   
   public void setPropagation(String type)
   {
      if ( !"none".equals(type) )
      {
         if ( !"begin".equals(type) )
         {
            getChildren().add( new UIConversationId() );
         }
         if ( !"continue".equals(type) )
         {
            initUiConversationPropagation();
            uiConversationPropagation.setType(type);
         }
      }
   }
   
   public void setPageflow(String type)
   {
      initUiConversationPropagation();
      uiConversationPropagation.setPageflow(type);
   }

}

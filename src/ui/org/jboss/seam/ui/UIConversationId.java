package org.jboss.seam.ui;

import javax.faces.component.UIParameter;

import org.jboss.seam.core.Conversation;

public class UIConversationId extends UIParameter
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIConversationId";
   
   @Override
   public String getName()
   {
      return "conversationId";
   }
   
   @Override
   public Object getValue()
   {
      return Conversation.instance().getId();
   }

}

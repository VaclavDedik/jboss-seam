package org.jboss.seam.ui.component;

import javax.faces.component.UIParameter;

import org.jboss.seam.core.Manager;

/*
 * This component not available as a tag
 */
public class UIConversationIsLongRunning extends UIParameter
{
   
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ConversationIsLongRunning";
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   @Override
   public String getName()
   {
      return Manager.instance().getConversationIsLongRunningParameter();
   }
   
   @Override
   public Object getValue()
   {
      return Manager.instance().isReallyLongRunningConversation();
   }

}

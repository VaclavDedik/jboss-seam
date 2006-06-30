package org.jboss.seam.ui;

import javax.faces.component.UIParameter;

import org.jboss.seam.core.Manager;

public class UIConversationIsLongRunning extends UIParameter
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIConversationIsLongRunning";
   
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

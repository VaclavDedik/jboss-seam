package org.jboss.seam.ui.component;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Manager;

/*
 * This component not available as a tag
 */
public abstract class UIConversationIsLongRunning extends UIParameter
{
   
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ConversationIsLongRunning";
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.ConversationIsLongRunning";
   
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
   
   public static UIConversationIsLongRunning newInstance() {
      return (UIConversationIsLongRunning) FacesContext.getCurrentInstance().getApplication().createComponent(COMPONENT_TYPE);
   }
}

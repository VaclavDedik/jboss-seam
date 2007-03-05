package org.jboss.seam.pages;

import org.jboss.seam.core.Manager;
import org.jboss.seam.util.Id;

public class SyntheticConversationIdParameter implements ConversationIdParameter
{
   public String getParameterName()
   {
      return Manager.instance().getConversationIdParameter();
   }
   
   public String getParameterValue()
   {
      return Manager.instance().getCurrentConversationId();
   }
   
   public String getInitialConversationId()
   {
      return Id.nextId();  
   }
}

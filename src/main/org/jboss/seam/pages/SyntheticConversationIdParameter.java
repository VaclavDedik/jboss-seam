package org.jboss.seam.pages;

import java.util.Map;

import org.jboss.seam.core.Manager;
import org.jboss.seam.util.Id;

public class SyntheticConversationIdParameter implements ConversationIdParameter
{
   public String getName()
   {
      return null;
   }
   
   public String getParameterName()
   {
      return Manager.instance().getConversationIdParameter();
   }
   
   public String getParameterValue()
   {
      return Manager.instance().getCurrentConversationId();
   }
   
   public String getInitialConversationId(Map parameters)
   {
      return Id.nextId();  
   }
   
   public String getConversationId()
   {
      return Id.nextId();
   }
   
   public String getRequestConversationId(Map parameters)
   {
      return Manager.getRequestParameterValue( parameters, getParameterName() );      
   }
}

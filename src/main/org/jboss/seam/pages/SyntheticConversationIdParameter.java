package org.jboss.seam.pages;

import javax.faces.context.FacesContext;

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
   
   public String getInitialConversationId()
   {
      return Id.nextId();  
   }
   
   public String getRequestConversationId()
   {
      FacesContext ctx = FacesContext.getCurrentInstance();
      
      String value = (String) ctx.getExternalContext().getRequestParameterMap().get(getParameterName());      
      
      return value != null ? value : Manager.instance().getCurrentConversationId();
   }
}

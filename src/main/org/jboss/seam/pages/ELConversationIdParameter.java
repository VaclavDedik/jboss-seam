package org.jboss.seam.pages;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Expressions.ValueBinding;

/**
 * Represents a conversation parameter that can be used to create a "natural"
 * conversation ID, by defining a &lt;conversation/&gt; entry in pages.xml. 
 *  
 * @author Shane Bryzak
 */
public class ELConversationIdParameter implements ConversationIdParameter
{
   private String name;
   private String paramName;
   private ValueBinding vb;
   
   public ELConversationIdParameter(String name, String paramName, String expression)
   {
      this.name = name;
      this.paramName = paramName;
      
      this.vb = expression != null ? Expressions.instance().createValueBinding(expression) : null;
   }
   
   public String getName()
   {
      return name;
   }
   
   public String getParameterName()
   {
      return paramName;
   }
   
   public String getInitialConversationId()
   {
      FacesContext ctx = FacesContext.getCurrentInstance();
      
      String value = (String) ctx.getExternalContext().getRequestParameterMap().get(paramName);
      
      if (value == null)
      {
         return null;
         
//          TODO - redirect to no-conversation-view-id ?
//         return Id.nextId();
      }
      else
      {
         return String.format("%s:%s", name, value);
      }
   }
   
   public String getRequestConversationId()
   {
      return getInitialConversationId();
   }

   public String getParameterValue()
   {
      Object value = vb.getValue();
      if (value != null)
      {
         return vb.getValue().toString();
      }
      else
      {      
         String conversationId = Manager.instance().getCurrentConversationId();
         if (conversationId != null)
         {
            int idx = conversationId.indexOf(':');
            if (idx != -1)
            {
               return conversationId.substring(idx + 1);
            }
            else
            {
               return conversationId;
            }
         }
         else
         {
            return null;
         }
      }

   }
}

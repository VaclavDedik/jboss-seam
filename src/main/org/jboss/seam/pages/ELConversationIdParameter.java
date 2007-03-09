package org.jboss.seam.pages;

import java.util.Map;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.util.Id;

/**
 * Represents a conversation parameter that can be used to create a "natural"
 * conversation ID, by defining a &lt;conversation/&gt; entry in pages.xml. 
 *  
 * @author Shane Bryzak
 */
public class ELConversationIdParameter implements ConversationIdParameter
{
   private String name;
   private String parameterName;
   private ValueBinding vb;
   
   public ELConversationIdParameter(String name, String paramName, String expression)
   {
      this.name = name;
      this.parameterName = paramName;
      
      this.vb = expression != null ? Expressions.instance().createValueBinding(expression) : null;
   }
   
   public String getName()
   {
      return name;
   }
   
   public String getParameterName()
   {
      return parameterName;
   }
   
   public String getInitialConversationId(Map parameters)
   {
      String id = getRequestConversationId(parameters);
      return id==null ? Id.nextId() : id;
   }
   
   public String getRequestConversationId(Map parameters)
   {
      String value = Manager.getRequestParameterValue(parameters, parameterName);
      if (value==null)
      {
         return null;
      }
      else
      {
         return name + ':' + value;
      }
   }

   public String getParameterValue()
   {
      Object value = vb.getValue();
      if (value != null)
      {
         //TODO: use a JSF converter!
         return vb.getValue().toString();
      }
      /*else
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
         }*/
         else
         {
            return null;
         }
      //}

   }
}

package org.jboss.seam.pages;

public interface ConversationIdParameter
{
   String getName();
   String getParameterName();
   String getParameterValue();
  
   String getInitialConversationId();
   String getRequestConversationId();
}

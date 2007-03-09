package org.jboss.seam.pages;

import java.util.Map;

public interface ConversationIdParameter
{
   String getName();
   String getParameterName();
   String getParameterValue();
   String getConversationId();
   String getInitialConversationId(Map parameters);
   String getRequestConversationId(Map parameters);
}

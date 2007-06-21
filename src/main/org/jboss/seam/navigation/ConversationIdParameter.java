package org.jboss.seam.navigation;

import java.util.Map;

/**
 * A strategy for propagating conversations across links
 * and redirects
 * 
 */
public interface ConversationIdParameter
{
   String getName();
   String getParameterName();
   String getParameterValue();
   String getConversationId();
   String getInitialConversationId(Map parameters);
   String getRequestConversationId(Map parameters);
}

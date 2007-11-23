package org.jboss.seam.ui.renderkit;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UIConversationName;

/**
 * @author Pete Muir
 *
 */
public class ConversationNameRendererBase extends CommandButtonParameterRendererBase
{
   
   private static LogProvider log = Logging.getLogProvider(ConversationNameRendererBase.class);
   private static final String PARAMETER_NAME = "conversationName";

   @Override
   protected LogProvider getLog()
   {
      return log;
   }

   @Override
   protected String getParameterName()
   {
      return PARAMETER_NAME;
   }

   @Override
   protected Class getComponentClass()
   {
      return UIConversationName.class;
   }

}

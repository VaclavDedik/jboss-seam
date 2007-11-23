package org.jboss.seam.ui.renderkit;




import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UIConversationPropagation;

/**
 * @author Pete Muir
 *
 */
public class ConversationPropagationRendererBase extends CommandButtonParameterRendererBase
{   
   private static LogProvider log = Logging.getLogProvider(ConversationPropagationRendererBase.class);
   private static final String PARAMETER_NAME = "conversationPropagation";
 
   @Override
   protected Class getComponentClass()
   {
      return UIConversationPropagation.class;
   }

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
}
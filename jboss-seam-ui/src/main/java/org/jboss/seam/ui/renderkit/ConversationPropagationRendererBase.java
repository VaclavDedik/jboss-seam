package org.jboss.seam.ui.renderkit;




import javax.faces.component.UIComponent;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UIConversationPropagation;
import org.richfaces.cdk.annotations.JsfRenderer;

/**
 * @author Pete Muir
 *
 */
@JsfRenderer(type="org.jboss.seam.ui.ConversationPropagationRenderer", family="org.jboss.seam.ui.ConversationPropagationRenderer")
public class ConversationPropagationRendererBase extends CommandButtonParameterRendererBase
{   
   
   private static LogProvider log = Logging.getLogProvider(ConversationPropagationRendererBase.class);
 
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
   protected String getParameterName(UIComponent component)
   {
      return ((UIConversationPropagation) component).getName();
   }
}
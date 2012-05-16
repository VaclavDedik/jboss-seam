package org.jboss.seam.ui.renderkit;

import javax.faces.component.UIComponent;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UIConversationName;
import org.richfaces.cdk.annotations.JsfRenderer;

/**
 * @author Pete Muir
 *
 */
@JsfRenderer(type="org.jboss.seam.ui.ConversationNameRenderer", family="org.jboss.seam.ui.ConversationNameRenderer")
public class ConversationNameRendererBase extends CommandButtonParameterRendererBase
{
   
   private static LogProvider log = Logging.getLogProvider(ConversationNameRendererBase.class);
   

   @Override
   protected LogProvider getLog()
   {
      return log;
   }

   @Override
   protected String getParameterName(UIComponent component)
   {
      return ((UIConversationName) component).getName();
   }

   @Override
   protected Class getComponentClass()
   {
      return UIConversationName.class;
   }

}

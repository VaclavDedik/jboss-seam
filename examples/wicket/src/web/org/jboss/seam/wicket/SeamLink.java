package org.jboss.seam.wicket;

import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.ValueMap;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;

public abstract class SeamLink extends Link
{
   
   public SeamLink(String id)
   {
      super(id);
   }
   
   public SeamLink(String id, IModel model)
   {
      super(id, model);
   }
   
   @Override
   protected CharSequence getURL()
   {
      return getRequestCycle().urlFor(this, ILinkListener.INTERFACE, getParameterMap());
   }
   
   private ValueMap getParameterMap()
   {
      ValueMap valueMap = new ValueMap();
      if (Conversation.instance().isLongRunning())
      {
         valueMap.add(Manager.instance().getConversationIdParameter(), Conversation.instance().getId());
      }
      return valueMap;
   }

}

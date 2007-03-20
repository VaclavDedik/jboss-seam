/**
 * License Agreement.
 *
 * Ajax4jsf 1.1 - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.seam.ui.component;

import javax.faces.component.UIParameter;

import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pages;
import org.jboss.seam.pages.Page;

/**
 * JSF component class
 *
 */
public class UIConversationId extends UIParameter {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.ConversationId";
	
	private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ConversationId";
	
   private String viewId;
   
	@Override
	public String getFamily()
	{
	   return COMPONENT_FAMILY;
	}
   
   
   @Override
   public String getName()
   {
      Conversation conversation = Conversation.instance();
      if (viewId!=null && ( !conversation.isNested() || conversation.isLongRunning() ) )
      {
         return Pages.instance().getPage(viewId)
                     .getConversationIdParameter()
                     .getParameterName();
      }
      else
      {
         return Manager.instance().getConversationIdParameter();
      }
   }
   
   @Override
   public Object getValue()
   {
      Conversation conversation = Conversation.instance();
      if ( !conversation.isNested() || conversation.isLongRunning() )
      {
         if (viewId!=null)
         {
            Page page = Pages.instance().getPage(viewId);
            return page.getConversationIdParameter().getParameterValue();
         }
         else
         {
            return conversation.getId();
         }
      }
      else
      {
         return conversation.getParentId();
      }
   }

   public String getViewId()
   {
      return viewId;
   }

   public void setViewId(String viewId)
   {
      this.viewId = viewId;
   }
   
}

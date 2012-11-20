package org.jboss.seam.ui.component;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.jboss.seam.navigation.ConversationIdParameter;
import org.jboss.seam.navigation.Pages;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * Embeds the natural conversation ID into the request.
 *
 * @author Shane Bryzak
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.ConversationName",value="Set the conversation name for a command link or button (or similar JSF control)."),
family="org.jboss.seam.ui.ConversationName", type="org.jboss.seam.ui.ConversationName",generate="org.jboss.seam.ui.component.html.HtmlConversationName", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="conversationName", handler="org.jboss.seam.ui.handler.CommandButtonParameterComponentHandler"), 
renderer = @JsfRenderer(type="org.jboss.seam.ui.ConversationNameRenderer", family="org.jboss.seam.ui.ConversationNameRenderer"),
attributes = {"javax.faces.component.UIParameter.xml" })
public abstract class UIConversationName extends UIParameter {
	
	private static final String COMPONENT_TYPE = "org.jboss.seam.ui.ConversationName";
   
   @Override
   public String getName()
   {
      return "conversationName";
   }
   
   @Override
   public Object getValue()
   {
      ConversationIdParameter param = Pages.instance().getConversationIdParameter(super.getValue().toString());      
      return param != null ? param.getConversationId() : null;
   }
   
   public static UIConversationName newInstance() {
      return (UIConversationName) FacesContext.getCurrentInstance().getApplication().createComponent(COMPONENT_TYPE);
   }
   
}

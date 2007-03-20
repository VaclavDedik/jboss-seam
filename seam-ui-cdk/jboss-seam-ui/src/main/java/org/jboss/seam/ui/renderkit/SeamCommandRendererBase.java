package org.jboss.seam.ui.renderkit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Pages;
import org.jboss.seam.pages.Page;
import org.jboss.seam.ui.component.UIAction;
import org.jboss.seam.ui.component.UIButton;
import org.jboss.seam.ui.component.UIConversationId;
import org.jboss.seam.ui.component.UIConversationIsLongRunning;
import org.jboss.seam.ui.component.UIConversationPropagation;
import org.jboss.seam.ui.component.UISeamCommandBase;
import org.jboss.seam.ui.component.UISelection;
import org.jboss.seam.ui.component.UITaskId;
import org.jboss.seam.ui.util.HTML;

// TODO Do this in a template with utility methods
// TODO Put the common stuff between s:button and s:link in a common file
public abstract class SeamCommandRendererBase extends AjaxComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIButton.class;
   }
   
   public abstract void writeStart(ResponseWriter writer, FacesContext facesContext, UISeamCommandBase seamCommand) throws IOException;
   
   public abstract void writeEnd(ResponseWriter writer, FacesContext facesContext, UISeamCommandBase seamCommand) throws IOException;
   
   @Override
   protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      if ( !component.isRendered() ) return;
      
      UISeamCommandBase seamCommand = (UISeamCommandBase) component;
      
      writeStart(writer, context, seamCommand);
      
      writer.writeAttribute("id", seamCommand.getClientId(context), "id");

      String viewId = seamCommand.getView();
      if (viewId == null)
      {
         viewId = Pages.getViewId(context);
      }
      
      String url = context.getApplication().getViewHandler().getActionURL(context, viewId);
      String encodedUrl = context.getExternalContext().encodeActionURL(url);
      encodedUrl = Pages.instance().encodeScheme(viewId, context, encodedUrl);
      
      String characterEncoding = context.getResponseWriter().getCharacterEncoding();
      boolean first = true;
      Set<String> usedParameters = new HashSet<String>();
      
      boolean conversationIdEncoded = false;
      Page page = Pages.instance().getPage(viewId);
      
      for ( Object child: seamCommand.getChildren() )
      {
         if (child instanceof UIParameter)
         {
            UIParameter uip = (UIParameter) child;
            if ( uip.getValue()!=null )
            {
               encodedUrl += getParameterString(characterEncoding, uip, first);
               first = false;
               
               if ( uip.getName().equals( page.getConversationIdParameter().getParameterName() ) )
               {
                  conversationIdEncoded = true;
               }
            }
            usedParameters.add( uip.getName() );
         }
      }
      
      if (viewId!=null)
      {
         Map<String, Object> pageParameters = Pages.instance().getConvertedParameters(context, viewId, usedParameters);
         for ( Map.Entry<String, Object> me: pageParameters.entrySet() )
         {
            UIParameter uip = new UIParameter();
            uip.setName( me.getKey() );
            uip.setValue( me.getValue() );
            encodedUrl += getParameterString(characterEncoding, uip, first);
            first = false;

            if (!conversationIdEncoded && me.getKey().equals(page.getConversationIdParameter().getParameterName()))
            {
               conversationIdEncoded = true;
            }
         }
      }
      
      if ( seamCommand.getAction() != null || seamCommand.getOutcome() != null )
      {
         
         UIAction uiAction = new UIAction();
         uiAction.setAction( seamCommand.getAction() ==null ? seamCommand.getOutcome() : seamCommand.getAction().getExpressionString() );
         encodedUrl += getParameterString(characterEncoding, uiAction, first);
         first = false;
      }
      
      if ( "default".equals(seamCommand.getPropagation()) || "join".equals(seamCommand.getPropagation()) || "nest".equals(seamCommand.getPropagation()) || "end".equals(seamCommand.getPropagation()) )
      {
         //always add the id, since conversations could begin after link is rendered
         if ( !conversationIdEncoded )
         {
            UIConversationId uiConversationId = new UIConversationId();
            uiConversationId.setViewId(viewId);
            encodedUrl += getParameterString(characterEncoding, uiConversationId, first);
            first = false;
         }
         if ( Conversation.instance().isLongRunning() || Conversation.instance().isNested() )
         {
            encodedUrl += getParameterString(characterEncoding, new UIConversationIsLongRunning(), first);
         }
      }
      
      if ( "join".equals(seamCommand.getPropagation()) || "nest".equals(seamCommand.getPropagation()) || "begin".equals(seamCommand.getPropagation()) || "end".equals(seamCommand.getPropagation()) )
      {
         UIConversationPropagation uiPropagation = new UIConversationPropagation();
         uiPropagation.setType(seamCommand.getPropagation());
         uiPropagation.setPageflow(seamCommand.getPageflow());
         encodedUrl  += getParameterString(characterEncoding, uiPropagation, first);
         first = false;
      }
      
      // TODO What is this all about
      ValueBinding taskInstanceValueBinding = seamCommand.getValueBinding("taskInstance");
      if (taskInstanceValueBinding!=null)
      {
         UITaskId uiTaskId = new UITaskId();
         uiTaskId.setValueBinding("taskInstance", taskInstanceValueBinding);
         encodedUrl  += getParameterString(characterEncoding, uiTaskId, first);
         first = false;
      }
      
      UISelection uiSelection = seamCommand.getSelection();
      if (uiSelection!=null)
      {
         encodedUrl += getParameterString(characterEncoding, uiSelection, first);
         first = false;
      }
            
      if (seamCommand.getFragment() != null)
      {
         encodedUrl += '#' + seamCommand.getFragment();
      }
      
      String onclick = seamCommand.getOnclick();
      if (onclick==null)
      {
         onclick = "";
      }
      else if ( onclick.length()>0 && !onclick.endsWith(";") )
      {
          onclick += ";";
      }
      if ( !seamCommand.disabled() )
      {
         onclick += "location.href='" + encodedUrl + "'";
      }
      writer.writeAttribute("onclick", onclick, null);
      org.jboss.seam.ui.util.HTML.renderHTMLAttributes(writer, seamCommand, HTML.BUTTON_PASSTHROUGH_ATTRIBUTES_WITHOUT_DISABLED_AND_ONCLICK);
      
      writeEnd(writer, context, seamCommand);
      writer.flush();
      
   }
   
   @SuppressWarnings("deprecation")
   private static String getParameterString(String characterEncoding, UIParameter param, boolean first) 
         throws UnsupportedEncodingException
   {
      Object value = param.getValue();
      String strValue = value==null ? "" : value.toString();
      String encoded = characterEncoding==null ? 
            URLEncoder.encode(strValue) : //to work around what appears to be a bug in ADF
            URLEncoder.encode(strValue, characterEncoding);
      return (first ? '?' : '&') + param.getName() + '=' + encoded;
   }

}

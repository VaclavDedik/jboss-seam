package org.jboss.seam.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.faces.component.UIParameter;

import org.ajax4jsf.ajax.html.HtmlLoadStyle;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Pages;
import org.jboss.seam.ui.resource.StyleResource;

public class UILoadStyle extends HtmlLoadStyle
{

   @Override
   public Object getSrc()
   {
      String src = StyleResource.WEB_RESOURCE_PATH + super.getSrc();

      UIConversationId uiConversationId = new UIConversationId();
      uiConversationId.setViewId(Pages.getViewId(getFacesContext()));
      try
      {
         src += getParameterString(getFacesContext().getResponseWriter().getCharacterEncoding(), uiConversationId, true);
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      return src;
   }
   
   @SuppressWarnings("deprecation")
   private String getParameterString(String characterEncoding, UIParameter param, boolean first) 
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
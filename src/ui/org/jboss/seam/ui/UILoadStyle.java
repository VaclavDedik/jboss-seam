package org.jboss.seam.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.ajax4jsf.ajax.html.HtmlLoadStyle;
import org.jboss.seam.core.Pages;
import org.jboss.seam.ui.resource.StyleResource;

public class UILoadStyle extends HtmlLoadStyle
{
   
   // TODO Come up with better name for this
   private boolean isolated;

   @Override
   public Object getSrc()
   {
      String src = StyleResource.WEB_RESOURCE_PATH + super.getSrc();

      UIConversationId uiConversationId = new UIConversationId();
      uiConversationId.setViewId(Pages.getViewId(getFacesContext()));
      try
      {
         src += getParameterString(getFacesContext().getResponseWriter().getCharacterEncoding(), uiConversationId, true);
         if (isIsolated())
         {
            UIComponent namingContainer = getParentNamingContainer(this);
            if (namingContainer != null)
            {
               UIParameter idPrefix = new UIParameter();
               idPrefix.setName("idPrefix");
               idPrefix.setValue(namingContainer.getClientId(getFacesContext()));
               src += getParameterString(getFacesContext().getResponseWriter().getCharacterEncoding(), idPrefix, false);
            }
         }
         
         
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
   
   public boolean isIsolated()
   {
      return isolated;
   }
   
   public void setIsolated(boolean isolated)
   {
      this.isolated = isolated;
   }
   
   private UIComponent getParentNamingContainer(UIComponent cmp)
   {
      if (cmp == null)
      {
         return null;
      }
      else if (cmp instanceof NamingContainer)
      {
         return cmp;
      }
      else
      {
         return getParentNamingContainer(cmp.getParent());
      }
   }

}
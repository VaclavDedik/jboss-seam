package org.jboss.seam.ui.component;

import java.io.UnsupportedEncodingException;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.ajax4jsf.ajax.html.HtmlLoadStyle;
import org.jboss.seam.core.Pages;
import org.jboss.seam.ui.resource.StyleResource;
import org.jboss.seam.ui.util.UrlBuilder;

public abstract class UILoadStyle extends HtmlLoadStyle
{

   @Override
   public Object getSrc()
   {

      UIConversationId uiConversationId = UIConversationId.newInstance();
      uiConversationId.setViewId(Pages.getViewId(getFacesContext()));
      try
      {
         UrlBuilder urlBuilder = new UrlBuilder(StyleResource.WEB_RESOURCE_PATH + super.getSrc());
         urlBuilder.addParameter(uiConversationId);
         if (isIsolated())
         {
            UIComponent namingContainer = getParentNamingContainer(this);
            if (namingContainer != null)
            {
               UIParameter idPrefix = new UIParameter();
               idPrefix.setName("idPrefix");
               urlBuilder.addParameter("idPrefix", namingContainer.getClientId(getFacesContext()));
            }
         }
         return urlBuilder.getEncodedUrl(); 
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public abstract boolean isIsolated();
   
   
   public abstract void setIsolated(boolean isolated);
   
   
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
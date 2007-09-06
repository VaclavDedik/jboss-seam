package org.jboss.seam.ui.component;

import java.io.UnsupportedEncodingException;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.ajax4jsf.component.html.HtmlLoadStyle;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.ui.resource.StyleResource;
import org.jboss.seam.ui.util.UrlBuilder;
import org.jboss.seam.util.Reflections;

public abstract class UILoadStyle extends HtmlLoadStyle
{

   @Override
   public Object getSrc()
   {

      UIConversationId uiConversationId = UIConversationId.newInstance();
      uiConversationId.setViewId(Pages.getViewId(getFacesContext()));
      try
      {
         UrlBuilder urlBuilder = new UrlBuilder(StyleResource.WEB_RESOURCE_PATH + super.getSrc(), null, FacesContext.getCurrentInstance().getResponseWriter().getCharacterEncoding());
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
   
   public static UILoadStyle newInstance() {
      // Avoid runtime dep on a4j
      try
      {
         return (UILoadStyle) Reflections.classForName("org.jboss.seam.ui.component.html.HtmlLoadStyle").newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error loading UILoadStyle");
      }
   }

}

package org.jboss.seam.ui.util;

import java.io.UnsupportedEncodingException;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.jboss.seam.navigation.Page;
import org.jboss.seam.navigation.Pages;

public class ViewUrlBuilder extends UrlBuilder
{

   private Page page;

   public ViewUrlBuilder(String viewId, String fragment)
   {
      super(fragment);
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext,
               viewId);
      String encodedUrl = facesContext.getExternalContext().encodeActionURL(url);
      encodedUrl = Pages.instance().encodeScheme(viewId, facesContext, encodedUrl);
      setUrl(encodedUrl);
      
      page = Pages.instance().getPage(viewId);
   }

   public void addParameter(UIParameter parameter) throws UnsupportedEncodingException
   {
      String name = parameter.getName();
      boolean append = true;
      if (!(name.equals(page.getConversationIdParameter().getParameterName())
               && getParameters().containsKey(name)))
      {
        super.addParameter(parameter);
      }
   }

   
}
/**
 * 
 */
package org.jboss.seam.ui.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Pages;
import org.jboss.seam.pages.Page;
import org.jboss.seam.ui.component.UISeamCommandBase;

public class UrlBuilder
{
   /**
    * 
    */
   private final UISeamCommandBase base;

   private String encodedUrl;

   private Map<String, String> parameters;

   private String fragment;

   private String characterEncoding;

   private Page page;

   public UrlBuilder(UISeamCommandBase base, String viewId, String fragment)
   {
      this.base = base;
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext,
               viewId);
      String encodedUrl = facesContext.getExternalContext().encodeActionURL(url);
      encodedUrl = Pages.instance().encodeScheme(viewId, facesContext, encodedUrl);
      characterEncoding = facesContext.getResponseWriter().getCharacterEncoding();
      page = Pages.instance().getPage(viewId);
      this.encodedUrl = url;
      this.fragment = fragment;
      this.parameters = new HashMap<String, String>();
   }

   private String urlEncode(String value) throws UnsupportedEncodingException
   {
      return characterEncoding == null ? URLEncoder.encode(value) : URLEncoder.encode(value,
               characterEncoding);
   }

   public void addParameter(UIParameter parameter) throws UnsupportedEncodingException
   {
      String value = parameter.getValue() == null ? "" : parameter.getValue().toString();
      String name = parameter.getName();
      boolean append = true;
      if (name.equals(page.getConversationIdParameter().getParameterName())
               && parameters.containsKey(name))
      {
         append = false;
      }
      if (append)
      {
         parameters.put(name, urlEncode(value));
      }
   }

   private String getParameters()
   {
      String params = "";
      for (String key : parameters.keySet())
      {
         params += "&" + key + "=" + parameters.get(key);
      }
      if (!"".equals(params))
      {
         params = "?" + params.substring(1);
      }
      return params;
   }

   private String getFragment()
   {
      if (fragment != null && !"".equals(fragment))
      {
         return "#" + fragment;
      }
      else
      {
         return "";
      }
   }

   public String getEncodedUrl()
   {
      return encodedUrl + getParameters() + getFragment();
   }
}
package org.jboss.seam.ui.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

public class UrlBuilder
{
   private String url;

   private String fragment;
   private String characterEncoding;
   
   private Map<String, String> parameters;

   protected UrlBuilder(String fragment)
   {
      this.fragment = fragment;
      parameters = new HashMap<String, String>();
      FacesContext facesContext = FacesContext.getCurrentInstance();
      characterEncoding = facesContext.getResponseWriter().getCharacterEncoding();
   }
   
   public UrlBuilder(String url, String fragment)
   {
      this(fragment);
      setUrl(url);
   }
   
   protected void setUrl(String url)
   {
      this.url = url;
   }
   
   protected String urlEncode(String value) throws UnsupportedEncodingException
   {
      return characterEncoding == null ? URLEncoder.encode(value) : URLEncoder.encode(value,
               characterEncoding);
   }

   protected String getFragment()
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
      return url + getParametersAsString() + getFragment();
   }
   
   protected String getParametersAsString()
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
   
   protected Map<String, String> getParameters()
   {
      return parameters;
   }
   
   public void addParameter(String name, String value) throws UnsupportedEncodingException
   {
      parameters.put(name, urlEncode(value));
   }
   
   public void addParameter(UIParameter parameter) throws UnsupportedEncodingException
   {
      String value = parameter.getValue() == null ? "" : parameter.getValue().toString();
      String name = parameter.getName();
      addParameter(name, value);
   }

}

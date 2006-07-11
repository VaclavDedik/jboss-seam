package org.jboss.seam.ui;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.CacheException;
import org.jboss.seam.core.PojoCache;

public class UICache extends UIComponentBase
{
   
   private static final Log log = LogFactory.getLog(UICache.class);
   
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Cache";
   
   private String key;
   private String region;

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   private String evaluateKey(FacesContext facesContext)
   {
      ValueBinding keyValueBinding = getValueBinding("key");
      return keyValueBinding==null ? key : keyValueBinding.getValue(facesContext).toString();
   }
   
   private boolean isEnabled(FacesContext facesContext)
   {
      ValueBinding ifValueBinding = getValueBinding("enabled");
      return ifValueBinding==null || (Boolean) ifValueBinding.getValue(facesContext);
   }

   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
      ResponseWriter response = facesContext.getResponseWriter();
      boolean enabled = isEnabled(facesContext);
      if (enabled)
      {
         String key = evaluateKey(facesContext);
         String cachedContent = getFromCache(key);
         if (cachedContent==null)
         {
            log.debug("rendering from scratch: " + key);
            StringWriter stringWriter = new StringWriter();
            ResponseWriter cachingResponseWriter = response.cloneWithWriter(stringWriter);
            facesContext.setResponseWriter(cachingResponseWriter);
            renderChildren(facesContext, this);
            facesContext.setResponseWriter(response);
            String output = stringWriter.getBuffer().toString();
            response.write(output);
            putInCache(key, output);
         }
         else
         {
            log.debug("rendering from cache: " + key);
            response.write("<!-- cached content for: ");
            response.write(key);
            response.write(" -->");
            response.write(cachedContent);
            response.write("<!-- end of cached content -->");
         }
      }
      else
      {
         renderChildren(facesContext, this);
      }
   }

   private void putInCache(String key, String content)
   {
      try
      {
         PojoCache.instance().put(region, key, content);
      }
      catch (CacheException ce)
      {
         log.error("error accessing cache", ce);
      }
   }

   private String getFromCache(String key)
   {
      try
      {
         return (String) PojoCache.instance().get(region, key);
      }
      catch (CacheException ce)
      {
         log.error("error accessing cache", ce);
         return null;
      }
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
   
   private static void renderChildren(FacesContext facesContext, UIComponent component)
         throws IOException
   {
      List children = component.getChildren();
      for (int j = 0, size = component.getChildCount(); j < size; j++)
      {
         UIComponent child = (UIComponent) children.get(j);
         if (child.isRendered())
         {
            child.encodeBegin(facesContext);
            if (child.getRendersChildren())
            {
               child.encodeChildren(facesContext);
            }
            else
            {
               renderChildren(facesContext, child);
            }
            child.encodeEnd(facesContext);
         }
      }
   }

   public String getKey()
   {
      return key;
   }

   public void setKey(String key)
   {
      this.key = key;
   }

   public String getRegion()
   {
      return region;
   }

   public void setRegion(String region)
   {
      this.region = region;
   }

}

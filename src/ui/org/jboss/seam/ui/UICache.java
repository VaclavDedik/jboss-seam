package org.jboss.seam.ui;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.cache.CacheException;
import org.jboss.seam.core.PojoCache;

public class UICache extends UIComponentBase
{
   
   private static final LogProvider log = Logging.getLogProvider(UICache.class);
   
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Cache";
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UICache";
   
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
      ValueBinding enabledValueBinding = getValueBinding("enabled");
      return enabledValueBinding==null || (Boolean) enabledValueBinding.getValue(facesContext);
   }

   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
      if ( !isRendered() ) return;
      
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
            JSF.renderChildren(facesContext, this);
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
         JSF.renderChildren(facesContext, this);
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

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      key = (String) values[1];
      region = (String) values[1];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[3];
      values[0] = super.saveState(context);
      values[1] = key;
      values[2] = region;
      return values;
   }

}

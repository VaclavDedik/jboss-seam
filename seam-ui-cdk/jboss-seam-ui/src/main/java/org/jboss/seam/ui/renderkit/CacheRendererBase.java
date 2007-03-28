package org.jboss.seam.ui.renderkit;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.core.PojoCache;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UICache;

public class CacheRendererBase extends AjaxComponentRendererBase
{
   
   private static final LogProvider log = Logging.getLogProvider(UICache.class);
   
   @Override
   protected Class getComponentClass()
   {
      return UICache.class;
   }
   
   @Override
   protected void doEncodeChildren(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UICache cache = (UICache) component;
      if (cache.isEnabled())
      {
         String key = cache.getKey();
         String cachedContent = getFromCache(key, cache.getRegion());
         if (cachedContent == null)
         {
            log.debug("rendering from scratch: " + key);
            StringWriter stringWriter = new StringWriter();
            ResponseWriter cachingResponseWriter = writer.cloneWithWriter(stringWriter);
            context.setResponseWriter(cachingResponseWriter);
            renderChildren(context, component);
            context.setResponseWriter(writer);
            String output = stringWriter.getBuffer().toString();
            writer.write(output);
            putInCache(key, cache.getRegion(), output);
         }
         else
         {
            log.debug("rendering from cache: " + key);
            writer.write("<!-- cached content for: ");
            writer.write(key);
            writer.write(" -->");
            writer.write(cachedContent);
            writer.write("<!-- end of cached content -->");
         }
      }
      else
      {
         renderChildren(context, component);
      }
   }
   

   private static void putInCache(String key, String region, String content)
   {
      try
      {
         PojoCache.instance().put(region, key, content);
      }
      catch (Exception ce)
      {
         log.error("error accessing cache", ce);
      }
   }

   private static String getFromCache(String key, String region)
   {
      try
      {
         return (String) PojoCache.instance().get(region, key);
      }
      catch (Exception ce)
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

}

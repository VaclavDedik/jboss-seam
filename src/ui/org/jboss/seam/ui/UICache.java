package org.jboss.seam.ui;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
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
         Command[] commands = getFromCache(key);
         if (commands==null)
         {
            log.debug("rendering from scratch: " + key);
            CachingResponseWriter cachingResponseWriter = new CachingResponseWriter(response);
            facesContext.setResponseWriter(cachingResponseWriter);
            renderChildren(facesContext, this);
            facesContext.setResponseWriter(response);
            putInCache( key, cachingResponseWriter.getCommands() );
         }
         else
         {
            log.debug("rendering from cache: " + key);
            for (Command cmd: commands) cmd.apply(response);
         }
      }
      else
      {
         renderChildren(facesContext, this);
      }
   }

   private void putInCache(String key, Command[] commands)
   {
      try
      {
         PojoCache.instance().put(region, key, commands);
      }
      catch (CacheException ce)
      {
         log.error("error accessing cache", ce);
      }
   }

   private Command[] getFromCache(String key)
   {
      try
      {
         return (Command[]) PojoCache.instance().get(region, key);
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

   static interface Command extends Serializable
   {
      public void apply(ResponseWriter responseWriter) throws IOException;
   }
   
   static final class CachingResponseWriter extends ResponseWriter
   {
      private final ResponseWriter responseWriter;
      private List<Command> commandList = new ArrayList<Command>();
      
      public Command[] getCommands()
      {
         return commandList.toArray( new Command[commandList.size()] );
      }

      private static final class WriteCommand implements Command
      {
         private final int off;

         private final int len;

         private final char[] cbuf;

         private WriteCommand(int off, int len, char[] cbuf)
         {
            this.off = off;
            this.len = len;
            this.cbuf = cbuf;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.write(cbuf, off, len);
         }
      }

      private static final class WriteAttributeCommand implements Command
      {
         private final String property;

         private final Object value;

         private final String name;

         private WriteAttributeCommand(String property, Object value, String name)
         {
            this.property = property;
            this.value = value;
            this.name = name;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.writeAttribute(name, value, property);
         }
      }

      private static final class WriteCommentCommand implements Command
      {
         private final Object comment;

         private WriteCommentCommand(Object comment)
         {
            this.comment = comment;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.writeComment(comment);
         }
      }

      private static final class WriteCharsCommand implements Command
      {
         private final char[] text;

         private final int len;

         private final int off;

         private WriteCharsCommand(char[] text, int len, int off)
         {
            this.text = text;
            this.len = len;
            this.off = off;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.writeText(text, off, len);
         }
      }

      private static final class WriteTextCommand implements Command
      {
         private final String property;

         private final Object text;

         private WriteTextCommand(String property, Object text)
         {
            this.property = property;
            this.text = text;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.writeText(text, property);
         }
      }

      private static final class WriteURIAttributeCommand implements Command
      {
         private final Object value;

         private final String property;

         private final String name;

         private WriteURIAttributeCommand(Object value, String property, String name)
         {
            this.value = value;
            this.property = property;
            this.name = name;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.writeURIAttribute(name, value, property);
         }
      }

      private static final class StartElementCommand implements Command
      {
         private final UIComponent component;

         private final String name;

         private StartElementCommand(UIComponent component, String name)
         {
            this.component = component;
            this.name = name;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.startElement(name, component);
         }
      }

      private static final class EndElementCommand implements Command
      {
         private final String name;

         private EndElementCommand(String name)
         {
            this.name = name;
         }

         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.endElement(name);
         }
      }

      private static final class EndDocumentCommand implements Command
      {
         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.endDocument();
         }
      }

      private static final class StartDocumentCommand implements Command
      {
         public void apply(ResponseWriter responseWriter) throws IOException
         {
            responseWriter.startDocument();
         }
      }

      private CachingResponseWriter(ResponseWriter writer)
      {
         this.responseWriter = writer;
      }

      @Override
      public ResponseWriter cloneWithWriter(Writer writer)
      {
         return new CachingResponseWriter( responseWriter.cloneWithWriter(writer) );
      }

      @Override
      public void startDocument() throws IOException
      {
         commandList.add( new StartDocumentCommand() );
         responseWriter.startDocument();
      }

      @Override
      public void endDocument() throws IOException
      {
         commandList.add( new EndDocumentCommand() );
         responseWriter.endDocument();
      }

      @Override
      public void startElement(final String name, final UIComponent component) throws IOException
      {
         commandList.add( new StartElementCommand(component, name) );
         responseWriter.startElement(name, component);
      }

      @Override
      public void endElement(final String name) throws IOException
      {
         commandList.add( new EndElementCommand(name) );
         responseWriter.endElement(name);
      }

      @Override
      public void writeAttribute(final String name, final Object value, final String property) throws IOException
      {
         commandList.add( new WriteAttributeCommand(property, value, name) );
         responseWriter.writeAttribute(name, value, property);
      }

      @Override
      public void writeComment(final Object comment) throws IOException
      {
         commandList.add( new WriteCommentCommand(comment) );
         responseWriter.writeComment(comment);
      }

      @Override
      public void writeText(final char[] text, final int off, final int len) throws IOException
      {
         commandList.add( new WriteCharsCommand(text, len, off) );
         responseWriter.writeText(text, off, len);
      }

      @Override
      public void writeText(final Object text, final String property) throws IOException
      {
         commandList.add( new WriteTextCommand(property, text) );
         responseWriter.writeText(text, property);
      }

      @Override
      public void writeURIAttribute(final String name, final Object value, final String property) throws IOException
      {
         commandList.add( new WriteURIAttributeCommand(value, property, name) );
         responseWriter.writeURIAttribute(name, value, property);
      }
      
      @Override
      public void write(final char[] cbuf, final int off, final int len) throws IOException
      {
         commandList.add( new WriteCommand(off, len, cbuf) );
         responseWriter.write(cbuf, off, len);
      }

      @Override
      public void flush() throws IOException
      {
         responseWriter.flush();
      }

      @Override
      public String getCharacterEncoding()
      {
         return responseWriter.getCharacterEncoding();
      }

      @Override
      public String getContentType()
      {
         return responseWriter.getContentType();
      }

      @Override
      public void close() throws IOException
      {
         responseWriter.close();
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

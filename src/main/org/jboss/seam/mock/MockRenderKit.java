package org.jboss.seam.mock;

import java.io.OutputStream;
import java.io.Writer;

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

public class MockRenderKit extends RenderKit 
{
   
   public static final MockRenderKit INSTANCE = new MockRenderKit();

   @Override
   public void addRenderer(String x, String y, Renderer renderer) 
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Renderer getRenderer(String x, String y) 
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResponseStateManager getResponseStateManager() 
   {
      return new MockResponseStateManager();
   }

   @Override
   public ResponseWriter createResponseWriter(Writer writer, String x, String y) 
   {
      return new MockResponseWriter();
   }
   
   @Override
   public ResponseStream createResponseStream(OutputStream stream) 
   {
      throw new UnsupportedOperationException();
   }
   
}

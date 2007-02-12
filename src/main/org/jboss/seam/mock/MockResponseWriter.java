package org.jboss.seam.mock;

import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

public class MockResponseWriter extends ResponseWriter
{

   @Override
   public ResponseWriter cloneWithWriter(Writer writer)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void endDocument() throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void endElement(String element) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void flush() throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public String getCharacterEncoding()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getContentType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void startDocument() throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void startElement(String element, UIComponent component) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void writeAttribute(String attribute, Object object, String string) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void writeComment(Object object) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void writeText(Object text, String string) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void writeText(char[] chars, int start, int end) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void writeURIAttribute(String attribute, Object object, String string) throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void close() throws IOException
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void write(char[] chars, int start, int end) throws IOException
   {
      // TODO Auto-generated method stub

   }

}

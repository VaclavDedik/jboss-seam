package org.jboss.seam.ui.renderkit;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.util.Resources;

import org.jboss.seam.ui.component.UIFormattedText;

import antlr.ANTLRException;

public class FormattedTextRendererBase extends org.ajax4jsf.framework.renderer.ComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIFormattedText.class;
   }
   
   @Override
   protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UIFormattedText formattedText = (UIFormattedText) component;
      if ( formattedText.getValue() == null) return;
      Reader r = new StringReader( (String) formattedText.getValue() );
      SeamTextLexer lexer = new SeamTextLexer(r);
      SeamTextParser parser = new SeamTextParser(lexer);
      try
      {
         parser.startRule();
      }
      catch (ANTLRException re)
      {
         throw new RuntimeException(re);
      }
      writer.write(parser.toString());
      
   }
   
   public Resources getResources()
   {
      return new Resources();
   }

}

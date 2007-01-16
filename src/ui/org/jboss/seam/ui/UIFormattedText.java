package org.jboss.seam.ui;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.jboss.seam.text.L;
import org.jboss.seam.text.P;

import antlr.ANTLRException;

public class UIFormattedText extends UIOutput             
{
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FormattedText";

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      if ( !isRendered() ) return;

      Reader r = new StringReader( (String) getValue() );
      L lexer = new L(r);
      P parser = new P(lexer);
      try
      {
         parser.startRule();
      }
      catch (ANTLRException re)
      {
         throw new RuntimeException(re);
      }

      context.getResponseWriter().write(parser.toString());
   }
   
}

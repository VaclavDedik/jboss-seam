package org.jboss.seam.wiki.core.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.core.Expressions;

public class UIStyle extends UIComponentBase
{

   public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.ui.UIStyle";

   private URL path;

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   public URL getPath()
   {
      return path;
   }

   public void setPath(URL path)
   {
      this.path = path;
   }

   // Would be nicer to use a A4J HeaderResourceRenderer for this, but
   // need to rework EL binding stuff if so.
   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      ResponseWriter output = context.getResponseWriter();
      // Render CSS
      InputStream is = path.openStream();
      if (is != null)
      {
         output.append("<style type=\"text/css\">\n");

         BufferedReader reader = new BufferedReader(new InputStreamReader(is));
         StringBuilder css = new StringBuilder();
         String line;
         while ((line = reader.readLine()) != null)
         {
            css.append(line);
            css.append("\n");
         }
         is.close();

         // Resolve any EL value binding expression present in CSS text
         StringBuffer resolvedCSS = new StringBuffer(css.length());
         Matcher matcher = Pattern.compile("#" + Pattern.quote("{") + "(.*)" + Pattern.quote("}"))
                  .matcher(css);

         // Replace with [Link Text=>Page Name] or replace with BROKENLINK "page
         // name"
         while (matcher.find())
         {
            Expressions.ValueBinding valueMethod = Expressions.instance().createValueBinding(
                     "#{" + matcher.group(1) + "}");
            String result = (String) valueMethod.getValue();
            if (result != null)
            {
               matcher.appendReplacement(resolvedCSS, result);
            }
            else
            {
               matcher.appendReplacement(resolvedCSS, "");
            }
         }
         matcher.appendTail(resolvedCSS);
         output.append(resolvedCSS);

         output.append("</style>\n");
      }
   }
   
   @Override
   public Object saveState(FacesContext arg0)
   {
      Object[] state = new Object[2];
      state[0] = super.saveState(arg0);
      state[1] = path;
      return state;
   }
   
   @Override
   public void restoreState(FacesContext arg0, Object arg1)
   {
      Object[] state = (Object[]) arg1;
      super.restoreState(arg0, state[0]);
      path = (URL) state[1];
   }

}

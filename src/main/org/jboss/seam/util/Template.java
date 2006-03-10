package org.jboss.seam.util;

import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;

public class Template {

   private static final Logger log = Logger.getLogger(Template.class);

   public static String render(String template) {
      FacesContext context = FacesContext.getCurrentInstance();
      StringTokenizer tokens = new StringTokenizer(template, "#${}", true);
      StringBuilder builder = new StringBuilder(template.length());
      while ( tokens.hasMoreTokens() )
      {
         String tok = tokens.nextToken();
         if ( "#".equals(tok) )
         {
            tokens.nextToken();
            String expression = "#{" + tokens.nextToken() + "}";
            try
            {
               Object value = context.getApplication().createValueBinding(expression).getValue(context);
               if (value!=null) builder.append(value);
            }
            catch (Exception e)
            {
               log.warn("exception rendering template: " + template, e);
            }
            tokens.nextToken();
         }
         else
         {
            builder.append(tok);
         }
      }
      return builder.toString();
   }

}

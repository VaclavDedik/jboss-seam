package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.STATELESS;

import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Intercept(NEVER)
@Scope(STATELESS)
@Name("interpolator")
public class Interpolator {

   private static final Log log = LogFactory.getLog(Interpolator.class);
   
   public static Interpolator instance()
   {
      return (Interpolator) Component.getInstance(Interpolator.class, true);
   }

   public String interpolate(String string) {
      FacesContext context = FacesContext.getCurrentInstance();
      StringTokenizer tokens = new StringTokenizer(string, "#${}", true);
      StringBuilder builder = new StringBuilder(string.length());
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
               log.warn("exception interpolating string: " + string, e);
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

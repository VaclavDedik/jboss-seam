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

/**
 * Interpolates EL expressions in Strings
 * 
 * @author Gavin King
 */
@Intercept(NEVER)
@Scope(STATELESS)
@Name("interpolator")
public class Interpolator {

   private static final Log log = LogFactory.getLog(Interpolator.class);
   
   public static Interpolator instance()
   {
      return (Interpolator) Component.getInstance(Interpolator.class, true);
   }
   
   /**
    * Replace all EL expressions in the form #{...} with their evaluated
    * values.
    * 
    * @param string a template
    * @return the interpolated string
    */
   public String interpolate(String string, Object... params) {
      if ( string.indexOf('#')<0 ) return string;
      if ( params.length>10 ) 
      {
         throw new IllegalArgumentException("more than 10 parameters");
      }
      
      FacesContext context = FacesContext.getCurrentInstance();
      StringTokenizer tokens = new StringTokenizer(string, "#{}", true);
      StringBuilder builder = new StringBuilder(string.length());
      while ( tokens.hasMoreTokens() )
      {
         String tok = tokens.nextToken();
         if ( "#".equals(tok) )
         {
            String nextTok = tokens.nextToken();
            if ( "{".equals(nextTok) )
            {
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
               int index = Integer.parseInt( nextTok.substring(0, 1) );
               if (index>=params.length) 
               {
                  throw new IllegalArgumentException("parameter index out of bounds: " + index + " in: " + string);
               }
               builder.append( params[index] );
               builder.append( nextTok.substring(1) );
            }
         }
         else
         {
            builder.append(tok);
         }
      }
      return builder.toString();
   }

}

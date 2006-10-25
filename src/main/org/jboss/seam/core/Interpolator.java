package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.STATELESS;

import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

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
      if ( Contexts.isApplicationContextActive() )
      {
         return (Interpolator) Component.getInstance(Interpolator.class, true);         
      }
      else
      {
         return new Interpolator(); //for unit testing
      }
   }
   
   /**
    * Replace all EL expressions in the form #{...} with their evaluated
    * values.
    * 
    * @param string a template
    * @return the interpolated string
    */
   public String interpolate(String string, Object... params) {
      if ( params.length>10 ) 
      {
         throw new IllegalArgumentException("more than 10 parameters");
      }
      
      if ( string.indexOf('#')>=0 )
      {
         string = interpolateExpressions(string, params);
      }
      if ( params.length>1 && string.indexOf('{')>=0 )
      {
         string = new MessageFormat(string, Locale.instance()).format(params);
      }
      return string;
   }

   private String interpolateExpressions(String string, Object... params)
   {
      StringTokenizer tokens = new StringTokenizer(string, "#{}", true);
      StringBuilder builder = new StringBuilder(string.length());
      while ( tokens.hasMoreTokens() )
      {
         String tok = tokens.nextToken();
         if ( "#".equals(tok) && tokens.hasMoreTokens() )
         {
            String nextTok = tokens.nextToken();
            if ( "{".equals(nextTok) )
            {
               String expression = "#{" + tokens.nextToken() + "}";
               try
               {
                  Object value = Expressions.instance().createValueBinding(expression).getValue();
                  if (value!=null) builder.append(value);
               }
               catch (Exception e)
               {
                  log.warn("exception interpolating string: " + string, e);
               }
               tokens.nextToken(); //the }
            }
            else 
            {
               int index;
               try
               {
                  index = Integer.parseInt( nextTok.substring(0, 1) );
                  if (index>=params.length) 
                  {
                     //log.warn("parameter index out of bounds: " + index + " in: " + string);
                     builder.append("#").append(nextTok);
                  }
                  else
                  {
                     builder.append( params[index] ).append( nextTok.substring(1) );
                  }
               }
               catch (NumberFormatException nfe)
               {
                  builder.append("#").append(nextTok);
               }
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

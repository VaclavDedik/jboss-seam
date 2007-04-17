package org.jboss.seam.ui.resource;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.servlet.AbstractResource;
import org.jboss.seam.util.Resources;

/**
 * Serve up stylesheets which are have been run through the EL
 * Interpolator.
 * @author pmuir
 *
 */

@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.ui.resource.styleResource")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class StyleResource extends AbstractResource
{
   
   public static final String WEB_RESOURCE_PATH = "/seam/resource/style";
   
   private static final String RESOURCE_PATH = "/style";

   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      String pathInfo = request.getPathInfo().substring(getResourcePath().length());
      
      InputStream in = Resources.getResourceAsStream(pathInfo);
      
      if (in != null)
      {
         try
         {
            Lifecycle.beginRequest( getServletContext(), request.getSession(), request );
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder css = new StringBuilder();
            String line;
            while ( (line = reader.readLine()) != null) {
                css.append(line);
                css.append("\n");
            }
            in.close();
            
            // Resolve any EL value binding expression present in CSS
            // This should be Interpolator.interpolate, but it seems to break on CSS
            StringBuffer resolvedCSS = new StringBuffer(css.length());
            Matcher matcher =
                Pattern.compile(
                    "#" +Pattern.quote("{") + "(.*)" + Pattern.quote("}")
                ).matcher(css);
   
            while (matcher.find()) {
                Expressions.ValueBinding valueMethod = Expressions.instance().createValueBinding("#{"+matcher.group(1)+"}");
                String result = (String)valueMethod.getValue();
                if (result != null) {
                    matcher.appendReplacement(resolvedCSS, result);
                } else {
                    matcher.appendReplacement(resolvedCSS, "");
                }
            }
            matcher.appendTail(resolvedCSS);
            response.getWriter().write(resolvedCSS.toString());
            
            response.getWriter().flush();
         } 
         finally
         {
            Lifecycle.endRequest();
         }
      }
      else
      {
         response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
      
   }

   @Override
   protected String getResourcePath()
   {
      return RESOURCE_PATH;
   }

}

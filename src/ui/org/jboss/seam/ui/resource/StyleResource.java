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
import org.jboss.seam.servlet.AbstractResource;
import org.jboss.seam.util.Resources;

/**
 * Serve up stylesheets which are have been run through the EL Interpolator.
 * 
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

   private static final Pattern EL_PATTERN = Pattern.compile("#" + Pattern.quote("{") + "(.*)"
            + Pattern.quote("}"));
   
   private static final Pattern ID_PATTERN = Pattern.compile("#([A-Za-z][A-Za-z0-9\\-\\_\\:\\.]*)");

   public static final String WEB_RESOURCE_PATH = "/seam/resource/style";

   private static final String RESOURCE_PATH = "/style";

   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response)
            throws IOException
   {
      String pathInfo = request.getPathInfo().substring(getResourcePath().length());

      InputStream in = Resources.getResourceAsStream(pathInfo);

      if (in != null)
      {
         try
         {
            Lifecycle.beginRequest(getServletContext(), request.getSession(), request);
            
            CharSequence css = readFile(in);
            
            css = parseEL(css);
            
            String idPrefix = request.getParameter("idPrefix");
            css = addIdPrefix(idPrefix, css);

            response.getWriter().write(css.toString());

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

   // Resolve any EL value binding expression present in CSS
   // This should be Interpolator.interpolate, but it seems to break on CSS
   private CharSequence parseEL(CharSequence string)
   {
      StringBuffer parsed = new StringBuffer(string.length());
      Matcher matcher =
          EL_PATTERN.matcher(string);

      while (matcher.find()) 
      {
          String result = Expressions.instance().createValueExpression("#{"+matcher.group(1)+"}", String.class).getValue();
          if (result != null) 
          {
              matcher.appendReplacement(parsed, result);
          } 
          else 
          {
              matcher.appendReplacement(parsed, "");
          }
      }
      matcher.appendTail(parsed);
      return parsed;
   }
   
   private CharSequence readFile(InputStream inputStream) throws IOException
   {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      StringBuilder css = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null)
      {
         css.append(line);
         css.append("\n");
      }
      inputStream.close();
      return css;
   }
   
   private CharSequence addIdPrefix(String idPrefix, CharSequence string)
   {
      StringBuffer parsed = new StringBuffer(string.length());
      if (idPrefix != null)
      {
         Matcher matcher = ID_PATTERN.matcher(string);
         while (matcher.find()) {
            String result = "#" + idPrefix + ":" + matcher.group(1);
            matcher.appendReplacement(parsed, result);
        }
        matcher.appendTail(parsed);
        return parsed;
      }
      else
      {
         return string;
      }
   }

   @Override
   protected String getResourcePath()
   {
      return RESOURCE_PATH;
   }

}

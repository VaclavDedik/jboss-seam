package org.jboss.seam.ui.resource;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.servlet.AbstractResource;
import org.jboss.seam.util.Resources;

@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.ui.resource.webResource")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class WebResource extends AbstractResource
{
   private static final String RESOURCE_PATH = "/web";
   
   @Override
   protected String getResourcePath()
   {
      return RESOURCE_PATH;
   }
   
   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      String pathInfo = request.getPathInfo().substring(getResourcePath().length()); 
      
      InputStream in = Resources.getResourceAsStream("/org/jboss/seam/ui/resource" + pathInfo);
      
      if (in != null)
      {
         byte[] buffer = new byte[1024];
         int read = in.read(buffer);
         while (read != -1)
         {
            response.getOutputStream().write(buffer, 0, read);
            read = in.read(buffer);
         }
         response.getOutputStream().flush();
      }
      else
      {
         response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
   }


}

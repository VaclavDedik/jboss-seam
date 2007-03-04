package org.jboss.seam.ui.resource;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.servlet.AbstractResource;
import org.jboss.seam.ui.resource.DynamicImageStore.ImageWrapper;

@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.ui.resource.dynamicImageResource")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class DynamicImageResource extends AbstractResource
{

 public static final String DYNAMIC_IMAGE_RESOURCE_PATH = "/seam/resource/dynamicImage";
   
   private static final String RESOURCE_PATH = "/dynamicImage";
   
   @Override
   protected String getResourcePath()
   {
      return RESOURCE_PATH;
   }
   
   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      String pathInfo = request.getPathInfo().substring(getResourcePath().length() + 1, request.getPathInfo().lastIndexOf(".")); 
      
     ImageWrapper image = DynamicImageStore.instance().remove(pathInfo);
      if (image != null)
      {
         response.setContentType(image.getContentType().getMimeType());
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentLength(image.getImage().length);
         ServletOutputStream os = response.getOutputStream();
         os.write(image.getImage());
         os.flush();
      }
      else
      {
         response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
   }

}

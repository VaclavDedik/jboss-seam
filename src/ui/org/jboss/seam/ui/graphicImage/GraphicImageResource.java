package org.jboss.seam.ui.graphicImage;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.faces.event.PhaseId;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.servlet.AbstractResource;
import org.jboss.seam.ui.graphicImage.GraphicImageStore.ImageWrapper;

@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.ui.graphicImage.graphicImageResource")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class GraphicImageResource extends AbstractResource
{

 public static final String GRAPHIC_IMAGE_RESOURCE_PATH = "/seam/resource/graphicImage";
   
   private static final String RESOURCE_PATH = "/graphicImage";
   
   @Override
   protected String getResourcePath()
   {
      return RESOURCE_PATH;
   }
   
   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      String pathInfo = request.getPathInfo().substring(getResourcePath().length() + 1,
               request.getPathInfo().lastIndexOf("."));

      // Set up Seam contexts
      HttpSession session = request.getSession(true);
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.setServletRequest(request);
      Lifecycle.beginRequest(getServletContext(), session, request);
      Manager.instance().restoreConversation(request.getParameterMap());
      Lifecycle.resumeConversation(session);
      Manager.instance().handleConversationPropagation(request.getParameterMap());
      
      try
      {
         ImageWrapper image = GraphicImageStore.instance().remove(pathInfo);
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

         // TODO: conversation timeout
         Manager.instance().endRequest(ContextAdaptor.getSession(session));
         Lifecycle.endRequest(session);
      }
      catch (Exception e)
      {
         Lifecycle.endRequest();
      }
      finally
      {
         Lifecycle.setServletRequest(null);
         Lifecycle.setPhaseId(null);
      }      
   }

}

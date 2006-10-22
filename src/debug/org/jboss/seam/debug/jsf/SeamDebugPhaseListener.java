package org.jboss.seam.debug.jsf;

import java.io.IOException;
import java.net.URL;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;

import com.sun.facelets.Facelet;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * Intercepts any request for a view-id like /debug.xxx and renders
 * the Seam debug page using facelets.
 * 
 * @author Gavin King
 */
public class SeamDebugPhaseListener implements PhaseListener
{

   public void beforePhase(PhaseEvent event)
   {
      Lifecycle.setPhaseId( event.getPhaseId() ); //since this gets called before SeamPhaseListener!
      
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String viewId = facesContext.getViewRoot().getViewId();
      if ( viewId!=null && viewId.startsWith("/debug.") && Init.instance().isDebug() )
      {
         try
         {
            URL url = SeamDebugPhaseListener.class.getClassLoader().getResource("META-INF/debug.xhtml");
            Facelet f = new DefaultFaceletFactory( new SAXCompiler(), new DefaultResourceResolver() ).getFacelet(url);
            UIViewRoot root = facesContext.getViewRoot();
            f.apply(facesContext, root);
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("text/html");
            ResponseWriter writer = facesContext.getRenderKit().createResponseWriter( response.getWriter(), "text/html", "UTF-8" );
            facesContext.setResponseWriter(writer);
            //root.renderAll();
            ComponentSupport.encodeRecursive(facesContext, root);
            writer.flush();
            facesContext.responseComplete();
         }
         catch (IOException ioe)
         {
            throw new RuntimeException(ioe);
         }
      }      
   }

   public void afterPhase(PhaseEvent event) {}

   public PhaseId getPhaseId()
   {
      return PhaseId.RENDER_RESPONSE;
   }
   
}

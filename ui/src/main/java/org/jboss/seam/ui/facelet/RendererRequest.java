package org.jboss.seam.ui.facelet;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.ResourceLoader;
import org.jboss.seam.jsf.DelegatingFacesContext;
import org.jboss.seam.mock.MockHttpServletRequest;
import org.jboss.seam.mock.MockHttpServletResponse;
import org.jboss.seam.ui.util.JSF;

import com.sun.facelets.Facelet;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;

public class RendererRequest
{
   
   private FacesContext originalFacesContext;
   private FacesContext facesContext;
   
   private MockHttpServletRequest request;
   private MockHttpServletResponse response;
   
   private StringWriter writer;
   
   private String viewId;
   
   public RendererRequest(String viewId)
   {
      this.viewId = viewId;
   }
   
   private void init()
   {
      request = new MockHttpServletRequest(HttpSessionManager.instance());
      response = new MockHttpServletResponse();
      
      // Generate the FacesContext from the JSF FacesContextFactory
      originalFacesContext = FacesContext.getCurrentInstance();
      facesContext = RendererFacesContextFactory.instance().getFacesContext(request, response);
      DelegatingFacesContext.setCurrentInstance(facesContext);
      
      // Create the viewRoot
      UIViewRoot newRoot = facesContext.getApplication().getViewHandler().createView(facesContext, viewId);
      facesContext.setViewRoot(newRoot);
      
      // Set the responseWriter to write to a buffer
      writer = new StringWriter();
      facesContext.setResponseWriter(facesContext.getRenderKit().createResponseWriter(writer,
      null, null));
   }
   
   private void cleanup()
   {
      facesContext.release();
      DelegatingFacesContext.setCurrentInstance(originalFacesContext);
      
      
      originalFacesContext = null;
      facesContext = null;
      request = null;
      response = null;
   }
   
   public void run() throws IOException
   {
      init();
      renderFacelet(facesContext, faceletForViewId(viewId));
      cleanup();
   }
   
   public String getOutput()
   {
      return writer.getBuffer().toString();
   }

   /**
    * Get a Facelet for a URL
    */
   protected Facelet faceletForViewId(String viewId) throws IOException
   {
      URL url = ResourceLoader.instance().getResource(viewId);
      if (url == null)
      {
         throw new IllegalArgumentException("resource doesn't exist: " + viewId);
      }
      return new DefaultFaceletFactory(FaceletCompiler.instance(), new DefaultResourceResolver())
               .getFacelet(url);
   }

   /**
    * Render a Facelet
    */
   protected void renderFacelet(FacesContext facesContext, Facelet facelet) throws IOException
   {
      UIViewRoot root = facesContext.getViewRoot();
      facelet.apply(facesContext, root);
      JSF.renderChildren(facesContext, root);  
   }
}

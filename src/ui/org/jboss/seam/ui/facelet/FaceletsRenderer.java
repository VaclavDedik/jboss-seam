package org.jboss.seam.ui.facelet;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.ui.JSF;
import org.jboss.seam.util.Resources;

import com.sun.facelets.Facelet;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;

@Scope(ScopeType.STATELESS)
@Intercept(InterceptionType.NEVER)
@Name("org.jboss.seam.core.renderer")
@Install(value = true, precedence = Install.BUILT_IN, classDependencies = { "com.sun.facelets.Facelet" })
public class FaceletsRenderer extends Renderer
{
   
   private class Context 
   {
      
      public Context(String viewId)
      {
         this.viewId = viewId;
      }
      
      private String viewId;
      
      private ClassLoader originalClassLoader;
      private ResponseWriter originalResponseWriter;
      private UIViewRoot originalViewRoot;
      private StringWriter writer = new StringWriter();
      
      public Context wrap() 
      {
         // Make sure we are using the correct ClassLoader
         originalClassLoader = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
         
         // If a FacesContext isn't available, set one up
         if (FacesContext.getCurrentInstance() == null)
         {
            MockFacesContext mockFacesContext = new MockFacesContext(new MockExternalContext())
                     .setCurrent();
            mockFacesContext.createViewRoot();
         }
         
         FacesContext facesContext = FacesContext.getCurrentInstance();
         
         // Wrap the ResponseWriter
         originalResponseWriter = facesContext.getResponseWriter();
         facesContext.setResponseWriter(facesContext.getRenderKit().createResponseWriter(writer,
                  null, null));
         
         // Create a new UIViewRoot
         originalViewRoot = facesContext.getViewRoot();
         UIViewRoot viewRoot = new UIViewRoot();
         viewRoot.setRenderKitId(facesContext.getApplication().getViewHandler().calculateRenderKitId(facesContext));
         viewRoot.setViewId(viewId);
         viewRoot.setLocale(originalViewRoot.getLocale());
         facesContext.setViewRoot(viewRoot);
         return this;
      }
      
      public void unwrap() 
      {
         FacesContext facesContext = FacesContext.getCurrentInstance();
         if (originalResponseWriter != null)
         {
            facesContext.setResponseWriter(originalResponseWriter);
         }
         if (originalViewRoot != null)
         {
            facesContext.setViewRoot(originalViewRoot);
         }
         Thread.currentThread().setContextClassLoader(originalClassLoader);
         
      }
      
      public String getWrittenOutput() 
      {
         return writer.getBuffer().toString(); 
      }
      
   }
   

   @Override
   public String render(String viewId)
   {
      Context context = new Context(viewId);
      try
      {
         context.wrap();
         FacesContext facesContext = FacesContext.getCurrentInstance();
         renderFacelet(facesContext, faceletForURL(resourceURL(viewId)));
         return context.getWrittenOutput();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         context.unwrap();
      }
   }

   protected URL resourceURL(String viewId)
   {

      URL url = Resources.getResource(viewId);

      if (url == null)
      {
         throw new IllegalArgumentException("resource doesn't exist: " + viewId);
      }

      return url;
   }

   protected Facelet faceletForURL(URL url) throws IOException
   {
      return new DefaultFaceletFactory(new SAXCompiler(), new DefaultResourceResolver())
               .getFacelet(url);
   }

   

   protected void renderFacelet(FacesContext facesContext, Facelet facelet) throws IOException
   {
      UIViewRoot root = facesContext.getViewRoot();
      facelet.apply(facesContext, root);
      JSF.renderChildren(facesContext, root);
   }
   
   

   
   
}

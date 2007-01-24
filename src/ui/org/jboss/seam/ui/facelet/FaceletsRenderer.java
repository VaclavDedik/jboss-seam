package org.jboss.seam.ui.facelet;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import java.net.URL;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletContext;

import org.jboss.seam.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.core.Renderer;

import org.jboss.seam.ui.JSF;
import org.jboss.seam.util.Resources;

import com.sun.facelets.Facelet;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;

@Scope(ScopeType.STATELESS)
@Intercept(InterceptionType.NEVER)
@Name("renderer")
@Install(value=true,precedence=Install.BUILT_IN,
        classDependencies={"com.sun.facelets.Facelet"})
public class FaceletsRenderer extends Renderer
{

    @Override
    public String render(String viewId) 
    {
        return render(viewId,
                      Thread.currentThread().getContextClassLoader());
    }
    

    public String render(String viewId, ClassLoader classLoader) {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        ResponseWriter originalWriter = null;

        try 
        {
            StringWriter stringWriter = new StringWriter();
            wrapResponseWriter(facesContext, stringWriter);

            renderFacelet(facesContext, faceletForURL(resourceURL(viewId)));

            return stringWriter.getBuffer().toString();
        } 
        catch (IOException e) 
        {
            throw new RuntimeException(e);
        } 
        finally 
        {
            if (originalWriter != null) 
            {
                facesContext.setResponseWriter(originalWriter);
            }
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

    protected Facelet faceletForURL(URL url) 
        throws IOException
    {
        return new DefaultFaceletFactory(new SAXCompiler(), 
                                         new DefaultResourceResolver()).getFacelet(url);
    }


    protected void wrapResponseWriter(FacesContext facesContext, Writer writer) 
    {
        ResponseWriter responseWriter = 
            facesContext.getRenderKit().createResponseWriter(writer, null, null);

        facesContext.setResponseWriter(responseWriter);
    }

    protected void renderFacelet(FacesContext facesContext, Facelet facelet) 
        throws IOException
    {
        UIViewRoot root = facesContext.getViewRoot();
        facelet.apply(facesContext, root);
        JSF.renderChildren(facesContext, root);
    }

}

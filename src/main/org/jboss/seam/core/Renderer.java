package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.*;

import java.io.IOException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.contexts.Contexts;

import com.sun.facelets.Facelet;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.tag.jsf.ComponentSupport;

@Name("renderer")
@Install(false)
public abstract class Renderer
{
    public abstract String render(String viewId);
    
    public static Renderer instance()
    {
        return (Renderer) Component.getInstance(Renderer.class, true);
    }
}

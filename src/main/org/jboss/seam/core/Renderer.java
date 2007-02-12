package org.jboss.seam.core;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("renderer")
@Install(false)
public abstract class Renderer
{
    public abstract String render(String viewId);
    
    public static Renderer instance()
    {
        return (Renderer) Component.getInstance(Renderer.class);
    }
}

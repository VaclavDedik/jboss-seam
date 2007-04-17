package org.jboss.seam.wiki.core.ui;

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class PluginComponentHandler extends ComponentHandler
{

   public PluginComponentHandler(ComponentConfig config)
   {
      super(config);
   }
   
   @Override
   protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent)
   {
      super.onComponentCreated(ctx, c, parent);
      parent.getAttributes().put(UIPlugin.NEXT_PLUGIN, c.getClientId(ctx.getFacesContext()));
   }

}

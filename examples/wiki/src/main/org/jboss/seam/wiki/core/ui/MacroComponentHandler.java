package org.jboss.seam.wiki.core.ui;

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

/**
 * Chaining up the macros. Still a bit of a riddle, what Pete did here.
 *
 * @author Pete Muir
 */
public class MacroComponentHandler extends ComponentHandler {

    public MacroComponentHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        super.onComponentCreated(ctx, c, parent);
        parent.getAttributes().put(UIMacro.NEXT_MACRO, c.getClientId(ctx.getFacesContext()));
    }

}

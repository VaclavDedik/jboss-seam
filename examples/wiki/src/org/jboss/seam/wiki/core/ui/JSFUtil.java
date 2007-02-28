package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;

import javax.faces.component.UIData;
import java.util.Collection;

/**
 * Adds stuff to and for JSF that should be there but isn't.
 */
@Name("jsfUtil")
public class JSFUtil {

    /**
     * Need to bind UI components to non-conversational backing beans.
     * That this is even needed makes no sense. Why can't I call the UI components
     * in the EL directly? Don't try components['id'], it won't work.
     */
    private UIData datatable;
    public UIData getDatatable() { return datatable; }
    public void setDatatable(UIData datatable) { this.datatable = datatable; }

    /**
     * Can't use col.size() in a value binding. Why can't I call arbitrary methods, even
     * with arguments, in a value binding? Java needs properties badly.
     */
    public static int sizeOf(Collection col) {
        return col.size();
    }
    
}

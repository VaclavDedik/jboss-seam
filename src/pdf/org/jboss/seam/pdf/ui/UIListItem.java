package org.jboss.seam.pdf.ui;

import javax.faces.context.*;
import com.lowagie.text.*;

public class UIListItem
    extends ITextComponent
{
    public static final String COMPONENT_TYPE = "org.jboss.seam.pdf.ui.UIListItem";

    ListItem listItem;
    
    public Object getITextObject() {
        return listItem;
    }

    public void createITextObject(FacesContext context) {
        listItem = new ListItem();
    }

    public void removeITextObject() {
        listItem = null;
    }

    public void handleAdd(Object o) {
        listItem.add(o);
    }
}

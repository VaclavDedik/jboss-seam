package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIListItem
    extends ITextComponent
{
    public static final String COMPONENT_TYPE = "org.jboss.seam.pdf.ui.UIListItem";

    ListItem listItem;
    
    public Object getITextObject() {
        return listItem;
    }

    public void createITextObject() {
        listItem = new ListItem();
    }

    public void removeITextObject() {
        listItem = null;
    }

    public void handleAdd(Object o) {
        listItem.add(o);
    }
}

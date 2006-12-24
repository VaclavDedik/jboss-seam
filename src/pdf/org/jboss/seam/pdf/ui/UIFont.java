package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIFont
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIParagraph";


    Font   font; 
    int    family = Font.UNDEFINED;
    int    size   = Font.UNDEFINED;
    String style; 

    public void setFamily(String name) {
        family = Font.getFamilyIndex(name);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Font getFont() {
        return font;
    }
       
            
    public Object getITextObject() {
        return null; // we don't add to this component, so skip
    }

    public void createITextObject() {
        font = new Font(family, size);
        if (style != null) {
            font.setStyle(style);
        }
    }

    public void add(Object o) {
        addToITextParent(o);
    }
}

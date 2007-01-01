package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIList
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIList";

    public static final String STYLE_NUMBERED  = "NUMBERED";
    public static final String STYLE_LETTERED  = "LETTERED";
    public static final String STYLE_GREEK     = "GREEK";
    public static final String STYLE_ROMAN     = "ROMAN";
    
    List list;

    String style; 
    String listSymbol;
    float indent = 20;;
    Boolean lowerCase = false;

    public void setStyle(String style) {
        this.style = style;
    }
    
    public void setIndent(float indent) {
        this.indent = indent;
    }

    public void setListSymbol(String listSymbol) {
        this.listSymbol = listSymbol;
    }

    /* for ROMAN,GREEK */
    public void setLowerCase(Boolean lowerCase) {
        this.lowerCase = lowerCase;
    }

    public Object getITextObject() {
        return list;
    }

    public void removeITextObject() {
        list = null;
    }

    public void createITextObject() {
        if (style != null) {
            if (style.equalsIgnoreCase(STYLE_ROMAN)) {
                list = new RomanList((int) indent); // int? bug in text?
                if (lowerCase != null) {
                    ((RomanList) list).setRomanLower(lowerCase);
                }
            } else if (style.equalsIgnoreCase(STYLE_GREEK)) {
                list = new GreekList((int)indent); // int? bug in itext?

                if (lowerCase != null) {
                    ((GreekList) list).setGreekLower(lowerCase);
                }
            } else if (style.equalsIgnoreCase(STYLE_NUMBERED)) {
                list = new List(true, indent);
                //setFirst(int)
            } else if (style.equalsIgnoreCase(STYLE_LETTERED)) {
                list = new List(false, true, indent);
                //setFirst(char)
            }
        }
        
        if (list == null) {
            list = new List(false, indent);
            if (listSymbol!=null) {
                list.setListSymbol(listSymbol);
            }
        }
    }

    public void handleAdd(Object o) {
        list.add(o);
    }
}

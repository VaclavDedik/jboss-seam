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

    List list;
    
    boolean numbered = false;
    boolean lettered = false;
    float   indent   = 20f;

    public void setNumbered(boolean numbered) {
        this.numbered = numbered;
    }

    public void setLettered(boolean lettered) {
        this.lettered = lettered;
    }

    public void setIndent(float indent) {
        this.indent = indent;
    }

    public Object getITextObject() {
        return list;
    }

    public void createITextObject() {
        list = new List(numbered, lettered, indent);
    }

    public void add(Object o) {
        list.add(o);
    }
}

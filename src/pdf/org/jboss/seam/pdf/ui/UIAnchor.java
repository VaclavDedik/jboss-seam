package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIAnchor
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIAnchor";

    Anchor anchor;
    
    String name;
    String reference;

    public void setName(String name) {
        this.name = name;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    public Object getITextObject() {
        return anchor;
    }

    public void createITextObject() {
        anchor = new Anchor();

        if (name != null) {
            anchor.setName(name);
        }

        if (reference != null) {
            anchor.setReference(reference);
        }
    }

    public void add(Object o) {
        anchor.add(o);
    }
}

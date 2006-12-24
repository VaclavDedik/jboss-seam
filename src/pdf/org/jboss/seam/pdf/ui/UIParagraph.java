package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIParagraph
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIParagraph";

    Paragraph paragraph;
    String alignment;

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public Object getITextObject() {
        return paragraph;
    }

    public void createITextObject() {
        Font font = getFont();
        if (font == null) {
            paragraph = new Paragraph();
        } else {
            paragraph = new Paragraph("", font);
        }

        if (alignment !=null) {
            paragraph.setAlignment(alignment);
        }
    }

    public void add(Object o) {
        paragraph.add(o);
    }
}

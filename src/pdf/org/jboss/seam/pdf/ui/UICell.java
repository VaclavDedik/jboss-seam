package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UICell
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UICell";

    PdfPCell cell;

    public Object getITextObject() {
        return cell;
    }

    public void removeITextObject() {
        cell = null;
    }

    public void createITextObject() {
        cell = new PdfPCell();
    }

    public void add(Object o) {
        if (o instanceof Element) {
            cell.addElement((Element) o);
        } else {
            throw new RuntimeException("Can't add " + o.getClass().getName() +
                                       " to cell");
        }
    }
}

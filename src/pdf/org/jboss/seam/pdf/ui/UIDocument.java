package org.jboss.seam.pdf.ui;

import org.jboss.seam.pdf.PDFStore;

import javax.faces.context.*;
import javax.faces.component.*;

import java.io.*;
import java.util.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIDocument 
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIDocument";

    Document document;
    ByteArrayOutputStream stream;
    String id;

    public Object getITextObject() {
        return document;
    }

    public void createITextObject() {
        document=new Document();
    }

    public void add(Object o) {
        if (o instanceof Element) {
            try {
                document.add((Element) o);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("cannot add " + o);
        }
    }

    @Override
    public void encodeBegin(FacesContext context) 
        throws IOException
    {
        id = PDFStore.instance().newId();
        document = new Document();
        stream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, stream);

            document.open();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        ResponseWriter response = context.getResponseWriter();
        response.startElement("html", this);
        response.startElement("head", this);
        response.startElement("meta", this);
        response.writeAttribute("http-equiv", "Refresh", null);
        response.writeAttribute("content", "0; URL=seam-pdf.seam?pdfId="+id, null);

        response.endElement("meta");
        response.endElement("head");

        response.startElement("body",this);
    }

    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        document.close();

        PDFStore.instance().saveData(id,stream.toByteArray());
        
        ResponseWriter response = context.getResponseWriter();
        response.endElement("body");
        response.endElement("html");
    }
}

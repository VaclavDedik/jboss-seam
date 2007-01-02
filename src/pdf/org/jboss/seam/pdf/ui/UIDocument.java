package org.jboss.seam.pdf.ui;

import org.jboss.seam.pdf.ITextUtils;
import org.jboss.seam.pdf.PDFStore;

import javax.faces.context.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIDocument 
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIDocument";

    Document document;
    ByteArrayOutputStream stream;
    String id;

    String title;
    String subject;
    String keywords;
    String author;
    String creator;
    
    String pageSize;
    String margins;
    Boolean marginMirroring;
 

    public void setMargins(String margins) {
       this.margins = margins;
    }
    
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
    
    public void setMarginMirroring(Boolean marginMirroring) {
        this.marginMirroring = marginMirroring;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Object getITextObject() {
        return document;
    }

    public void createITextObject(FacesContext context) {
        document = new Document();
        
        // most of this needs to be done BEFORE document.open();
        
        pageSize = (String) valueBinding(context, "pageSize", pageSize);
        if (pageSize != null) {
            document.setPageSize(ITextUtils.pageSizeValue(pageSize));
        }
        
        margins = (String) valueBinding(context, "margins", margins);
        if (margins != null) {
            float[] vals = ITextUtils.stringToFloatArray(margins);
            if (vals.length != 4) {
                throw new RuntimeException("margins must contain 4 float values");
            }

            document.setMargins(vals[0], vals[1], vals[2], vals[3]);           
        }
    
        marginMirroring = (Boolean) valueBinding(context, "marginMirroring", marginMirroring);
        if (marginMirroring != null) {
           document.setMarginMirroring(marginMirroring);
        }        
    }

    private void initMetaData(FacesContext context) {
        title = (String) valueBinding(context, "title", title);
        if (title != null) {
            document.addTitle(title);
        }
        
        subject = (String) valueBinding(context, "subject", subject);
        if (subject != null) {
            document.addSubject(subject);
        }
        
        keywords = (String) valueBinding(context, "keywords", keywords);
        if (keywords != null) {
            document.addKeywords(keywords);
        }
        
        author = (String) valueBinding(context, "author", author);
        if (author != null) {
            document.addAuthor(author);
        }
        
        creator = (String) valueBinding(context, "creator", creator);
        if (creator != null) {
            document.addCreator(creator);
        }
    }

    public void removeITextObject() {
        document = null;
    }

    public void handleAdd(Object o) {
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
        super.encodeBegin(context);
        
        id = PDFStore.instance().newId();
        stream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, stream);
            
            initMetaData(context);
            
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
    
        removeITextObject();
    }


}

package org.jboss.seam.pdf.ui;

import org.jboss.seam.pdf.ITextUtils;
import org.jboss.seam.pdf.DocumentStore;
import org.jboss.seam.pdf.DocumentStore.DocType;

import javax.faces.context.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.*;
import com.lowagie.text.rtf.RtfWriter2;

public class UIDocument 
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIDocument";
    
    
    
    Document document;
    ByteArrayOutputStream stream;
    String id;
    DocType docType;
    
    String type;
    String title;
    String subject;
    String keywords;
    String author;
    String creator;
    String orientation; 
    
    String pageSize;
    String margins;
    Boolean marginMirroring;
 
    public void setType(String type) {
        this.type = type;
    }
    
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
    
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    public Object getITextObject() {
        return document;
    }

    public void createITextObject(FacesContext context) {
        type = (String) valueBinding(context, "type", type);        
        docType = docTypeForName(type);
        
        document = new Document();
        // most of this needs to be done BEFORE document.open();
        
        pageSize = (String) valueBinding(context, "pageSize", pageSize);
        if (pageSize != null) {
            document.setPageSize(ITextUtils.pageSizeValue(pageSize));
        }
  
        System.out.println("page size was " + document.getPageSize().width() +
                "x" + document.getPageSize().height());
        orientation = (String) valueBinding(context, "orientation", orientation);  
        System.out.println("orientation: " + orientation);
        if (orientation != null) {
            if (orientation.equalsIgnoreCase("portrait")) {
                // do nothing
            } else if (orientation.equalsIgnoreCase("landscape")) {
                Rectangle currentSize = document.getPageSize();
                document.setPageSize(new Rectangle(currentSize.height(),
                                                   currentSize.width()));

            } else {
                throw new RuntimeException("orientation value " + orientation + "unknown");
            }
        }
        System.out.println("page size is " + document.getPageSize().width() +
                "x" + document.getPageSize().height());
        
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
        
        DocumentStore store = DocumentStore.instance();
        id = store.newId();
        stream = new ByteArrayOutputStream();
              
        try {
            switch (docType) {
            case PDF:
                PdfWriter.getInstance(document, stream);
                break;
            case RTF:
                RtfWriter2.getInstance(document, stream);
                break;
            case HTML:
                HtmlWriter.getInstance(document, stream);
                break;
            }

            
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
        
        String url = store.preferredUrlForContent(docType, id);
        response.writeAttribute("content", "0; URL=" + url, null);
 
        response.endElement("meta");
        response.endElement("head");

        response.startElement("body",this);
    }

    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        document.close();

        DocumentStore.instance().saveData(id,
                                          docType,
                                          stream.toByteArray());        

        ResponseWriter response = context.getResponseWriter();
        response.endElement("body");
        response.endElement("html");
    
        removeITextObject();
    }

    private DocType docTypeForName(String typeName) {    
        if (typeName != null) {
            if (typeName.equalsIgnoreCase(DocType.PDF.name())) {
                return DocType.PDF;
            } else if (typeName.equalsIgnoreCase(DocType.RTF.name())) {
                return DocType.RTF;
            } else if (typeName.equalsIgnoreCase(DocType.HTML.name())) {
                return DocType.HTML;
            }
        }
        return DocType.PDF;
    }
}

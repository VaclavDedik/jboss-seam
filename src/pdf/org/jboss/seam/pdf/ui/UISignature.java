package org.jboss.seam.pdf.ui;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jboss.seam.pdf.ITextUtils;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfWriter;

public class UISignature 
    extends ITextComponent 
{
    String field;
    String size;
    
    public void setField(String field) {
        this.field = field;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    @Override
    public void createITextObject(FacesContext context) {}

    @Override
    public void removeITextObject() {}
    
    @Override
    public Object getITextObject() {       
        return null;
    }

    @Override
    public void handleAdd(Object other) {
        throw new RuntimeException("PDF signature does not accept children");
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {              
        PdfWriter writer = findWriter();
        if (writer == null) {
            throw new RuntimeException("Cannot find PdfWriter - the document may not exist or may not be a pdf type");
        }
        
        PdfAcroForm form = writer.getAcroForm();   

        field = (String) valueBinding(context, "field", field);
        if (field == null) {
            throw new RuntimeException("signature field named is required");
        }
        
        size = (String) valueBinding(context, "size", size);
        if (size == null) {
            throw new RuntimeException("signature size is required");
        }
        float[] rect = ITextUtils.stringToFloatArray(size);
        if (rect.length != 4) {
            throw new RuntimeException("size must contain four numbers");
        }
        form.addSignature(field, rect[0], rect[1], rect[2], rect[3]);
        
        super.encodeEnd(context);
    }

    private PdfWriter findWriter() {
        UIDocument doc = (UIDocument) findITextParent(this, UIDocument.class);
        if (doc != null) {
            DocWriter writer = doc.getWriter();
           
            if (writer instanceof PdfWriter) {
                return (PdfWriter) writer;
            }
        }   
        return null;
    }    

}

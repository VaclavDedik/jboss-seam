package org.jboss.seam.pdf.ui;

import javax.faces.context.*;

import java.io.*;

import com.lowagie.text.*;

public class UIPage
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIPage";

    public Object getITextObject() {
        return null;
    }

    public void removeITextObject() {

    }

    public void createITextObject(FacesContext context) {
        
    }

    public void handleAdd(Object o) {
        addToITextParent(o);
    }

    @Override
    public void encodeBegin(FacesContext context) 
        throws IOException
    {
        super.encodeBegin(context);
        Document document = findDocument();
        if (document != null) {
            try {
                document.newPage();
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Cannot find parent document");
        }
    }

}

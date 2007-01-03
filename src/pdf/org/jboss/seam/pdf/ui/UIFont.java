package org.jboss.seam.pdf.ui;

import javax.faces.context.*;
import com.lowagie.text.*;

public class UIFont
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIParagraph";


    Font   font; 
    
    String familyName;
    int    size   = Font.UNDEFINED;
    String style; 

    public void setFamily(String familyName) {
        this.familyName = familyName;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Font getFont() {
        return font;
    }       
            
    public Object getITextObject() {
        return null; // we don't add to this component, so skip
    }

    public void removeITextObject() {
        font = null;
    }
    
    public void createITextObject(FacesContext context) {
        familyName = (String) valueBinding(context, "familyName", familyName);
        int family = (familyName==null) ? Font.UNDEFINED :  Font.getFamilyIndex(familyName);        
        size = (Integer) valueBinding(context, "size", size);        
        
        font = new Font(family, size);

        style = (String) valueBinding(context, "style", style);
        if (style != null) {
            font.setStyle(style);
        }
    }

    public void handleAdd(Object o) {
        addToITextParent(o);
    }
}

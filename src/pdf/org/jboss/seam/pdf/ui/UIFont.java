package org.jboss.seam.pdf.ui;

import java.awt.Color;

import javax.faces.context.*;

import org.jboss.seam.pdf.ITextUtils;

import com.lowagie.text.*;

public class UIFont
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIParagraph";

    Font   font; 
    
    String familyName;
    int    size   = Font.UNDEFINED;
    String style; 
    Color  color;

    public void setFamily(String familyName) {
        this.familyName = familyName;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStyle(String style) {
        this.style = style;
    }
    
    public void setColor(String color) {
        this.color = ITextUtils.colorValue(color);
    }

    @Override
    public Font getFont() {
        return font;
    }       
            
    @Override
    public Object getITextObject() {
        return null; // we don't add to this component, so skip
    }

    @Override
    public void removeITextObject() {
        font = null;
    }
    
    @Override
    public void createITextObject(FacesContext context) {
        familyName = (String) valueBinding(context, "familyName", familyName);
        int family = (familyName==null) ? Font.UNDEFINED :  Font.getFamilyIndex(familyName);        
        size = (Integer) valueBinding(context, "size", size);        
        
        font = new Font(family, size);

        style = (String) valueBinding(context, "style", style);
        if (style != null) {
            font.setStyle(style);
        }
        
        if (color != null) {
            font.setColor(color);
        }
    }

    @Override
    public void handleAdd(Object o) {
        addToITextParent(o);
    }
}

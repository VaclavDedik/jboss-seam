package org.jboss.seam.pdf.ui;

import javax.faces.context.*;
import com.lowagie.text.*;

public class UIChapter
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIChapter";
    
    Chapter chapter;
    
    Integer number = 1;
    
    public void setNumber(Integer number) { 
        this.number = number;
    }
    
    public Chapter getChapter() {
        return chapter;
    }
    
    public Object getITextObject() {
        return chapter;
    }

    public void removeITextObject() {
        chapter = null;
    }

    
    public void createITextObject(FacesContext context) {
        number = (Integer) valueBinding(context, "number", number);               
        chapter = new Chapter("",number);
    }

    public void handleAdd(Object o) {
        chapter.add(o);
    }
}

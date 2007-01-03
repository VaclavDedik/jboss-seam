package org.jboss.seam.pdf.ui;

import javax.faces.context.*;
import com.lowagie.text.*;

public class UISection
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UISection";

    Section section;
    
    public Object getITextObject() {
        return section;
    }

    public void removeITextObject() {
        section = null;
    }

    public void createITextObject(FacesContext context) {
        Chapter chapter = ((UIChapter)findITextParent(this, UIChapter.class)).getChapter();
        section = chapter.addSection(new Paragraph("*fake title*"), 1);
        section.setTitle(new Paragraph("*section title*"));
    }

    public void handleAdd(Object o) {
        section.add(o);
    }
}

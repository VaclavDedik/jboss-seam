package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

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

    public void createITextObject() {
        Chapter chapter = ((UIChapter)findITextParent(this, UIChapter.class)).getChapter();
        section = chapter.addSection(new Paragraph("*fake title*"), 1);
        section.setTitle(new Paragraph("*section title*"));
    }

    public void add(Object o) {
        section.add(o);
    }
}

package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIChapter
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIChapter";

    Chapter chapter;
    
    public Chapter getChapter() {
        return chapter;
    }
    public Object getITextObject() {
        return chapter;
    }

    public void removeITextObject() {
        chapter = null;
    }

    public void createITextObject() {
        chapter = new Chapter("*chapter title*",1);
    }

    public void handleAdd(Object o) {
        chapter.add(o);
    }
}

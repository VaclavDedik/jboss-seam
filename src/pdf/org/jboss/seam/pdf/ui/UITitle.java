package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import com.lowagie.text.Paragraph;

public class UITitle extends ITextComponent {
    @Override
    public void createITextObject(FacesContext context) {
        //
    }

    @Override
    public Object getITextObject() {      
        return null;
    }

    @Override
    public void handleAdd(Object other) {
        Paragraph paragraph = null;
        
        if (other instanceof Paragraph) {
            paragraph = (Paragraph) other;
        }
        
        if (paragraph == null) {
            throw new RuntimeException("title must be a paragraph");
        }
        
        UIChapter chapter = (UIChapter) findITextParent(this,UIChapter.class);
        if (chapter == null) {
            throw new RuntimeException("cannot find parent chapter for title");
        }
        
        chapter.getChapter().setTitle(paragraph);
    }

    @Override
    public void removeITextObject() {
        //
    }

}

package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIParagraph
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIParagraph";

    Paragraph paragraph;
    String alignment;

    
    Float firstLineIndent;
    Float extraParagraphSpace;
    Float leading;
    Float multipliedLeading;
    Float spacingBefore;
    Float spacingAfter;
    Float indentationLeft;
    Float indentationRight;

    Boolean keepTogether;

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public void setFirstLineIndent(Float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public void setExtraParagraphSpace(Float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }
    public void setLeading(Float leading) {
        this.leading = leading;
    }
    public void setMultipliedLeading(Float multipliedLeading) {
        this.multipliedLeading = multipliedLeading;
    }
    public void setSpacingBefore(Float spacingBefore) {
        this.spacingBefore = spacingBefore;
    }
    public void setSpacingAfter(Float spacingAfter) {
        this.spacingAfter = spacingAfter;
    }
    public void setIndentationLeft(Float indentationLeft) {
        this.indentationLeft = indentationLeft;
    }
    public void setIndentationRight(Float indentationRight) {
        this.indentationRight = indentationRight;
    }
    public void setKeepTogether(Boolean keepTogether) {
        this.keepTogether = keepTogether;
    }

    public Object getITextObject() {
        return paragraph;
    }

    public void removeITextObject() {
        paragraph = null;
    }

    public void createITextObject() {
        Font font = getFont();
        if (font == null) {
            paragraph = new Paragraph();
        } else {
            paragraph = new Paragraph("", font);
        }

        if (alignment != null) {
            paragraph.setAlignment(alignment);
        }
        
        if (firstLineIndent != null) {
            paragraph.setFirstLineIndent(firstLineIndent);
        }

        if (extraParagraphSpace != null) {
            paragraph.setExtraParagraphSpace(extraParagraphSpace);
        }
        if (leading != null) {
            if (multipliedLeading != null) {
                paragraph.setLeading(leading, multipliedLeading);
            } else {
                paragraph.setLeading(leading);
            }
        }
        if (spacingBefore != null) {
            paragraph.setSpacingBefore(spacingBefore);
        }
        if (spacingAfter != null) {
            paragraph.setSpacingAfter(spacingAfter);
        }
        if (indentationLeft != null) {
            paragraph.setIndentationLeft(indentationLeft);
        }
        if (indentationRight != null) {
            paragraph.setIndentationRight(indentationRight);
        }
        if (keepTogether != null) {
            paragraph.setKeepTogether(keepTogether);
        }
    }

    public void add(Object o) {
        paragraph.add(o);
    }
}

package org.jboss.seam.pdf.ui;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UITable
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UITable";

    PdfPTable table;
    int columns    = 1;
    Integer headerRows = 0;
    Integer footerRows = 0;

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setHeaderRows(Integer headerRows) {
        this.headerRows = headerRows;
    }

    public void setFooterRows(Integer footerRows) {
        this.footerRows = footerRows;
    }


    public Object getITextObject() {
        return table;
    }

    public void removeITextObject() {
        table = null;
    }

    public void createITextObject() {
        table = new PdfPTable(columns);
        if (headerRows != null) {
            table.setHeaderRows(headerRows.intValue());
        }
        if (footerRows != null) {
            table.setFooterRows(footerRows.intValue());
        }
    }

    public void add(Object o) {
        if (o instanceof PdfPCell) {
            table.addCell((PdfPCell) o);
        } else {
            throw new RuntimeException("Can't add " + o.getClass().getName() +
                                       " to table");
        }
    }
}

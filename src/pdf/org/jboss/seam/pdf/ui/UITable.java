package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jboss.seam.pdf.ITextUtils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UITable
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UITable";

    PdfPTable table;
    String  widths;
    int     columns    = 1;
    Integer headerRows = 0;
    Integer footerRows = 0;
    Float   widthPercentage;
    Integer horizontalAlignment;
    Boolean skipFirstHeader;
    Integer runDirection;
    Boolean lockedWidth;
    Boolean splitRows;
    Float   spacingBefore;
    Float   spacingAfter;
    Boolean extendLastRow;
    Boolean headersInEvent;
    Boolean splitLate;
    Boolean keepTogether;


    public void setWidths(String Widths) {
    	this.widths = Widths;
    }
    
    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setHeaderRows(Integer headerRows) {
        this.headerRows = headerRows;
    }

    public void setFooterRows(Integer footerRows) {
        this.footerRows = footerRows;
    }

    public void setExtendLastRow(Boolean extendLastRow) {
        this.extendLastRow = extendLastRow;
    }
    
    public void setHeadersInEvent(Boolean headersInEvent) {
        this.headersInEvent = headersInEvent;
    }
    
    public void setHorizontalAlignment(String horizontalAlignment) {
        this.horizontalAlignment = ITextUtils.alignmentValue(horizontalAlignment);
    }
    
    public void setKeepTogether(Boolean keepTogether) {
        this.keepTogether = keepTogether;
    }
    
    public void setLockedWidth(Boolean lockedWidth) {
        this.lockedWidth = lockedWidth;
    }
    
    public void setRunDirection(Integer runDirection) {
        this.runDirection = runDirection;
    }
    
    public void setSkipFirstHeader(Boolean skipFirstHeader) {
        this.skipFirstHeader = skipFirstHeader;
    }
    
    public void setSpacingAfter(Float spacingAfter) {
        this.spacingAfter = spacingAfter;
    }
    
    public void setSpacingBefore(Float spacingBefore) {
        this.spacingBefore = spacingBefore;
    }
    
    public void setSplitLate(Boolean splitLate) {
        this.splitLate = splitLate;
    }
    
    public void setSplitRows(Boolean splitRows) {
        this.splitRows = splitRows;
    }
    
    public void setTable(PdfPTable table) {
        this.table = table;
    }
    
    public void setWidthPercentage(Float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }
	
    
    
    public Object getITextObject() {
        return table;
    }
    
    public void removeITextObject() {
        table = null;
    }
   
    public void createITextObject(FacesContext context) {
        table = new PdfPTable(columns);
        
        if (widths != null) {
        	try {
				table.setWidths(ITextUtils.stringToFloatArray(widths));
			} catch (DocumentException e) {
				throw new RuntimeException(e);
			}       	
        }
        
        if (headerRows != null) {
            table.setHeaderRows(headerRows);
        }
        if (footerRows != null) {
            table.setFooterRows(footerRows);
        }
        if (widthPercentage!=null) {
            table.setWidthPercentage(widthPercentage);
        }
        if (horizontalAlignment!=null) {
            table.setHorizontalAlignment(horizontalAlignment);
        }
        if (skipFirstHeader!=null) {
            table.setSkipFirstHeader(skipFirstHeader);
        }
        if (runDirection!=null) {
            table.setRunDirection(runDirection);
        }
        if (lockedWidth!=null) {
            table.setLockedWidth(lockedWidth);
        }
        if (splitRows!=null) {
            table.setSplitRows(splitRows);
        }
        if (spacingBefore!=null) {
            table.setSpacingBefore(spacingBefore);
        }
        if (spacingAfter!=null) {
            table.setSpacingAfter(spacingAfter);
        }
        if (extendLastRow!=null) {
            table.setExtendLastRow(extendLastRow);
        }
        if (headersInEvent!=null) {
            table.setHeadersInEvent(headersInEvent);
        }
        if (splitLate!=null) {
            table.setSplitLate(splitLate);
        }
        if (keepTogether!=null) {
            table.setKeepTogether(keepTogether);
        }
    }
    
    public void handleAdd(Object o) {
        if (o instanceof PdfPCell) {
            table.addCell((PdfPCell) o);
        } else if (o instanceof PdfPTable) {
            table.addCell((PdfPTable) o);
        } else if (o instanceof Phrase) {
            table.addCell((Phrase) o);
        } else if (o instanceof Image) {
            table.addCell((Image) o);
        } else {
            throw new RuntimeException("Can't add " + o.getClass().getName() +
                                       " to table");
        }
    }

	public PdfPCell getDefaultCellFacet() {
		Object facet = facets.get("defaultCell");
		if (facet != null) {
		    if (!(facet instanceof PdfPCell)) {
		    	throw new RuntimeException("UITable defaultCell facet must be a PdfPCell - found " + facet.getClass());
		    }
		    return (PdfPCell) facet;
 		}
		return null;
	}
}

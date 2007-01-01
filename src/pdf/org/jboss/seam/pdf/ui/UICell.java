package org.jboss.seam.pdf.ui;

import org.jboss.seam.pdf.ITextUtils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;

public class UICell
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UICell";

    PdfPCell cell;
    String  horizontalAlignment;
    String  verticalAlignment;
    Float   padding;
    Float   paddingLeft;
    Float   paddingRight;
    Float   paddingTop;
    Float   paddingBottom;
    Boolean useBorderPadding;
    Float   leading;
    Float   multipliedLeading;
    Float   indent;
    Float   extraParagraphSpace;
    Float   fixedHeight;
    Boolean noWrap;
    Float   minimumHeight;
    Integer colspan;
    Float   followingIndent;
    Float   rightIndent;
    Integer spaceCharRatio;
    Integer runDirection;
    Integer arabicOptions;
    Boolean useAscender;
    Integer rotation; 
    Color borderColor;
    Color borderColorLeft;
    Color borderColorRight;
    Color borderColorTop;
    Color borderColorBottom;
    Color backgroundColor;
    Float grayFill;
    Float borderWidth;
    Float borderWidthLeft;
    Float borderWidthRight; 
    Float borderWidthTop;
    Float borderWidthBottom;
    
    public void setBorderWidth(Float borderWidth) {
		this.borderWidth = borderWidth;
	}

	public void setBorderWidthBottom(Float borderWidthBottom) {
		this.borderWidthBottom = borderWidthBottom;
	}

	public void setBorderWidthLeft(Float borderWidthLeft) {
		this.borderWidthLeft = borderWidthLeft;
	}

	public void setBorderWidthRight(Float borderWidthRight) {
		this.borderWidthRight = borderWidthRight;
	}

	public void setBorderWidthTop(Float borderWidthTop) {
		this.borderWidthTop = borderWidthTop;
	}

	public void setGrayFill(Float grayFill) {
        this.grayFill = grayFill;
    }

    public void setHorizontalAlignment(String horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public void setVerticalAlignment(String verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public void setPadding(Float padding) {
    	this.padding = padding;
    }
    
    public void setPaddingLeft(Float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingRight(Float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingTop(Float paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingBottom(Float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setUseBorderPadding(Boolean useBorderPadding) {
        this.useBorderPadding = useBorderPadding;
    }

    public void setLeading(Float leading) {
        this.leading = leading;
    }

    public void setMultipliedLeading(Float multipliedLeading) {
        this.multipliedLeading = multipliedLeading;
    }

    public void setIndent(Float indent) {
        this.indent = indent;
    }

    public void setExtraParagraphSpace(Float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }

    public void setFixedHeight(Float fixedHeight) {
        this.fixedHeight = fixedHeight;
    }

    public void setNoWrap(Boolean noWrap) {
        this.noWrap = noWrap;
    }

    public void setMinimumHeight(Float minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public void setColspan(Integer colspan) {
        this.colspan = colspan;
    }

    public void setFollowingIndent(Float followingIndent) {
        this.followingIndent = followingIndent;
    }

    public void setRightIndent(Float rightIndent) {
        this.rightIndent = rightIndent;
    }

    public void setSpaceCharRatio(Integer spaceCharRatio) {
        this.spaceCharRatio = spaceCharRatio;
    }

    public void setRunDirection(Integer runDirection) {
        this.runDirection = runDirection;
    }

    public void setArabicOptions(Integer arabicOptions) {
        this.arabicOptions = arabicOptions;
    }

    public void setUseAscender(Boolean useAscender) {
        this.useAscender = useAscender;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }
    
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = ITextUtils.colorValue(backgroundColor);
	}
	
	public void setBorderColor(String borderColor) {
    	this.borderColor = ITextUtils.colorValue(borderColor);
    }

	public void setBorderColorBottom(String borderColorBottom) {
		this.borderColorBottom =  ITextUtils.colorValue(borderColorBottom);
	}

	public void setBorderColorLeft(String borderColorLeft) {
		this.borderColorLeft = ITextUtils.colorValue(borderColorLeft)  ;
	}

	public void setBorderColorRight(String borderColorRight) {
		this.borderColorRight = ITextUtils.colorValue(borderColorRight);
	}

	public void setBorderColorTop(String borderColorTop) {
		this.borderColorTop = ITextUtils.colorValue(borderColorTop);
	}
	
    public Object getITextObject() {
        return cell;
    }

    public void removeITextObject() {
        cell = null;
    }

    public void createITextObject() {
    	PdfPCell defaultCell = getDefaultCellFromTable();
    	if (defaultCell != null) {
    	    cell = new PdfPCell(defaultCell);	   	 
    	} else {
            cell = new PdfPCell();
    	}
        if (horizontalAlignment != null) {
            cell.setHorizontalAlignment(ITextUtils.alignmentValue(horizontalAlignment));
        }
        if (verticalAlignment != null) {
            cell.setVerticalAlignment(ITextUtils.alignmentValue(verticalAlignment));
        }
        if (padding != null) {
        	cell.setPadding(padding);
        }
        if (paddingLeft != null) {
            cell.setPaddingLeft(paddingLeft);
        }
        if (paddingRight != null) {
            cell.setPaddingRight(paddingRight);
        }
        if (paddingTop != null) {
            cell.setPaddingTop(paddingTop);
        }
        if (paddingBottom != null) {
            cell.setPaddingBottom(paddingBottom);
        }
        if (useBorderPadding != null) {
            cell.setUseBorderPadding(useBorderPadding);
        }
        if (leading != null || multipliedLeading != null) {            
            cell.setLeading(leading == null         ? 0 : leading.floatValue(), 
                            multipliedLeading==null ? 0 : multipliedLeading.floatValue());
        }
        if (indent != null) {
            cell.setIndent(indent);
        }
        if (extraParagraphSpace != null) {
            cell.setExtraParagraphSpace(extraParagraphSpace);
        }
        if (fixedHeight != null) {
            cell.setFixedHeight(fixedHeight);
        }
        if (noWrap != null) {
            cell.setNoWrap(noWrap);
        }
        if (minimumHeight != null) {
            cell.setMinimumHeight(minimumHeight);
        }
        if (colspan != null) {
            cell.setColspan(colspan);
        }
        if (followingIndent != null) {
            cell.setFollowingIndent(followingIndent);
        }
        if (rightIndent != null) {
            cell.setRightIndent(rightIndent);
        }
        if (spaceCharRatio != null) {
            cell.setSpaceCharRatio(spaceCharRatio);
        }
        if (runDirection != null) {
            cell.setRunDirection(runDirection);
        }
        if (arabicOptions != null) {
            cell.setArabicOptions(arabicOptions);
        }
        if (useAscender != null) {
            cell.setUseAscender(useAscender);
        }
        if (rotation != null) {
            cell.setRotation(rotation);
        }
        if (backgroundColor!=null) {
        	cell.setBackgroundColor(backgroundColor);
        }
        if (borderColor!=null) {
        	cell.setBorderColor(borderColor);
        }
        if (borderColorLeft!=null) {
        	cell.setBorderColorLeft(borderColorLeft);
        }
        if (borderColorRight!=null) {
        	cell.setBorderColorRight(borderColorRight);
        }     
        if (borderColorTop!=null) {
        	cell.setBorderColorTop(borderColorTop);
        }
        if (borderColorBottom!=null) {
        	cell.setBorderColorBottom(borderColorBottom);
        }    
        if (borderWidth!=null) {
        	cell.setBorderWidth(borderWidth);
        }
        if (borderWidthLeft!=null) {
        	cell.setBorderWidthLeft(borderWidthLeft);
        }
        if (borderWidthRight!=null) {
        	cell.setBorderWidthRight(borderWidthRight);
        }     
        if (borderWidthTop!=null) {
        	cell.setBorderWidthTop(borderWidthTop);
        }
        if (borderWidthBottom!=null) {
        	cell.setBorderWidthBottom(borderWidthBottom);
        }    
        if (grayFill!=null) {
           cell.setGrayFill(grayFill);
        }
    }

    private PdfPCell getDefaultCellFromTable() {
    	UITable parentTable = (UITable) findITextParent(this, UITable.class);
    	if (parentTable != null) {
    		return parentTable.getDefaultCellFacet();
    	}
		return null;
	}

	public void handleAdd(Object o) {
        if (o instanceof Element) {
            cell.addElement((Element) o);
        } else {
            throw new RuntimeException("Can't add " + o.getClass().getName() +
                                       " to cell");
        }
    }

}

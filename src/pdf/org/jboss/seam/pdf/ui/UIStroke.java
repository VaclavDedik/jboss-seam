package org.jboss.seam.pdf.ui;

import java.awt.BasicStroke;
import java.awt.Stroke;

import javax.faces.component.UIComponentBase;

import org.jboss.seam.pdf.ITextUtils;

public class UIStroke 
    extends UIComponentBase 
{
    Float  width;
    String cap; // CAP_BUTT, CAP_ROUND, CAP_SQUARE
    String join; // JOIN_MITER, JOIN_ROUND, JOIN_BEVEL
    Float  miterLimit =1f;
    String dashString;
    Float  dashPhase = 0f;
    
    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getDash() {
        return dashString;
    }

    public void setDash(String dash) {
        this.dashString = dash;
    }

    public float getDashPhase() {
        return dashPhase;
    }

    public void setDashPhase(float dashPhase) {
        this.dashPhase = dashPhase;
    }

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

    public float getMiterlimit() {
        return miterLimit;
    }

    public void setMiterLimit(float miterLimit) {
        this.miterLimit = miterLimit;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
    
    public int capValue(String cap) {
        if (cap == null || cap.equalsIgnoreCase("butt")) {
            return BasicStroke.CAP_BUTT;
        } else if (cap.equalsIgnoreCase("round")) {
            return BasicStroke.CAP_ROUND;
        } else if (cap.equalsIgnoreCase("square")) {
            return BasicStroke.CAP_SQUARE;
        }
        throw new RuntimeException("invalid cap value: " + cap);        
    }
    
    public int joinValue(String join) {
        if (cap == null || cap.equalsIgnoreCase("mite")) {
            return BasicStroke.JOIN_MITER;
        } else if (cap.equalsIgnoreCase("round")) {
            return BasicStroke.JOIN_ROUND;
        } else if (cap.equalsIgnoreCase("bevel")) {
            return BasicStroke.JOIN_BEVEL;
        }
        throw new RuntimeException("invalid join value: " + cap);
    }
    

    @Override
    public String getFamily() {
        return ITextComponent.COMPONENT_FAMILY;
    }     
    
    public Stroke getStroke() {
        if (width == null) {
            return new BasicStroke();
        } else if (cap == null) {
            return new BasicStroke(getWidth());
        } else if (dashString == null) {
            if (miterLimit == null) {
                return new BasicStroke(getWidth(), capValue(getCap()), joinValue(getJoin()));
            } else {
                return new BasicStroke(getWidth(), capValue(getCap()), joinValue(getJoin()), miterLimit); 
            }
        } else {
            return new BasicStroke(getWidth(), 
                                   capValue(getCap()), 
                                   joinValue(getJoin()), 
                                   getMiterlimit(),
                                   ITextUtils.stringToFloatArray(getDash()), 
                                   getDashPhase());
        }
    }

}

package org.jboss.seam.pdf.ui;

import java.awt.Color;

import javax.faces.context.FacesContext;

import org.jboss.seam.pdf.ITextUtils;

import com.lowagie.text.Rectangle;

public abstract class UIRectangle 
extends ITextComponent 
{
    protected Color borderColor;
    protected Color borderColorLeft;
    protected Color borderColorRight;
    protected Color borderColorTop;
    protected Color borderColorBottom;
    protected Color backgroundColor;
    protected Float borderWidth;
    protected Float borderWidthLeft;
    protected Float borderWidthRight;
    protected Float borderWidthTop;
    protected Float borderWidthBottom;

    public UIRectangle() {
        super();
    }

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


    public void applyRectangleProperties(FacesContext context, Rectangle rectangle) {
        backgroundColor = (Color) valueBinding(context, "backgroundColor", backgroundColor);
        if (backgroundColor != null) {
            rectangle.setBackgroundColor(backgroundColor);
        }

        borderColor = (Color) valueBinding(context, "borderColor", borderColor);
        if (borderColor != null) {
            rectangle.setBorderColor(borderColor);
        }

        borderColorLeft = (Color) valueBinding(context, "borderColorLeft", borderColorLeft);
        if (borderColorLeft != null) {
            rectangle.setBorderColorLeft(borderColorLeft);
        }

        borderColorRight = (Color) valueBinding(context, "borderColorRight", borderColorRight);
        if (borderColorRight != null) {
            rectangle.setBorderColorRight(borderColorRight);
        }     

        borderColorTop = (Color) valueBinding(context, "borderColorTop", borderColorTop);
        if (borderColorTop != null) {
            rectangle.setBorderColorTop(borderColorTop);
        }

        borderColorBottom = (Color) valueBinding(context, "borderColorBottom", borderColorBottom);
        if (borderColorBottom != null) {
            rectangle.setBorderColorBottom(borderColorBottom);
        }    

        borderWidth = (Float) valueBinding(context, "borderWidth", borderWidth);
        if (borderWidth != null) {
            rectangle.setBorderWidth(borderWidth);
        }

        borderWidthLeft = (Float) valueBinding(context, "borderWidthLeft", borderWidthLeft);
        if (borderWidthLeft != null) {
            rectangle.setBorderWidthLeft(borderWidthLeft);
        }

        borderWidthRight = (Float) valueBinding(context, "borderWidthRight", borderWidthRight);
        if (borderWidthRight != null) {
            rectangle.setBorderWidthRight(borderWidthRight);
        }     

        borderWidthTop = (Float) valueBinding(context, "borderWidthTop", borderWidthTop);
        if (borderWidthTop != null) {
            rectangle.setBorderWidthTop(borderWidthTop);
        }

        borderWidthBottom = (Float) valueBinding(context, "borderWidthBottom", borderWidthBottom);
        if (borderWidthBottom != null) {
            rectangle.setBorderWidthBottom(borderWidthBottom);
        }    

    }

}
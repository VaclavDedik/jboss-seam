package org.jboss.seam.pdf.ui;

import java.awt.Color;
import javax.faces.context.FacesContext;

import org.jboss.seam.pdf.ui.ITextComponent;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;


/**
 * original implementation from JBSEAM-1155 by ivan
 */

public class UIBarCode 
    extends ITextComponent
{
    private String type;
    private String code;
    private Barcode barcode;
    private Image image;
    private Float xpos;
    private Float ypos;
    private Float rotDegrees;
    private String altText;
    private Float barHeight;
    private Float textSize;
    private Float minBarWidth;
    private Float barMultiplier;



    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Float getRotDegrees()
    {
        return rotDegrees;
    }

    public void setRotDegrees(Float rotDegrees)
    {
        this.rotDegrees = rotDegrees;
    }

    public Float getXpos()
    {
        return xpos;
    }

    public void setXpos(Float xpos)
    {
        this.xpos = xpos;
    }

    public Float getYpos()
    {
        return ypos;
    }

    public void setYpos(Float ypos)
    {
        this.ypos = ypos;
    }

    public String getAltText()
    {
        return altText;
    }

    public void setAltText(String altText)
    {
        this.altText = altText;
    }

    public Float getBarHeight()
    {
        return barHeight;
    }

    public void setBarHeight(Float barHeight)
    {
        this.barHeight = barHeight;
    }

    public Float getBarMultiplier()
    {
        return barMultiplier;
    }

    public void setBarMultiplier(Float barMultiplier)
    {
        this.barMultiplier = barMultiplier;
    }

    public Float getMinBarWidth()
    {
        return minBarWidth;
    }

    public void setMinBarWidth(Float minBarWidth)
    {
        this.minBarWidth = minBarWidth;
    }

    public Float getTextSize()
    {
        return textSize;
    }

    public void setTextSize(Float textSize)
    {
        this.textSize = textSize;
    }
    
    @Override
    public Object getITextObject()
    {
        return image;
    }

    @Override
    public void createITextObject(FacesContext context)
    {
        type = (String) valueBinding(context, "type", type);
        if (type != null) {
            if (type.equalsIgnoreCase("code128")) {
                barcode = new Barcode128();
            } else if (type.equalsIgnoreCase("code39")) {
                barcode = new Barcode39();
            } else if (type.equalsIgnoreCase("codabar")) {
                barcode = new BarcodeCodabar();
            } else if (type.equalsIgnoreCase("ean")) {
                barcode = new BarcodeEAN();
            } else if (type.equalsIgnoreCase("inter25")) {
                barcode = new BarcodeInter25();
            } else if (type.equalsIgnoreCase("postnet")) {
                barcode = new BarcodePostnet();
            }
        } else {
            barcode = new Barcode128();
        }
        
        code = (String) valueBinding(context, "code", code);
        if (code != null) {
            barcode.setCode(code);
        } else {
            barcode.setCode("Empty Code");
        }
        
        altText = (String) valueBinding(context, "altText", altText);
        if (altText != null) {
            barcode.setAltText(altText);
        }

        barHeight = (Float) valueBinding(context, "barHeight", barHeight);
        if (barHeight != null) {
            barcode.setBarHeight(barHeight);
        }

        barMultiplier = (Float) valueBinding(context, "barMultiplier", barMultiplier);
        if (barMultiplier != null) {
            barcode.setN(barMultiplier);
        }
        
        minBarWidth = (Float) valueBinding(context, "minBarWidth", minBarWidth);
        if (minBarWidth != null)
        {
            barcode.setX(minBarWidth);
        }

        try {
            image = Image.getInstance(barcode.createAwtImage(Color.BLACK, Color.WHITE), null);
            
            rotDegrees = ((Float) valueBinding(context, "rotDegrees", rotDegrees));
            if (rotDegrees != null) {
                image.setRotationDegrees(rotDegrees);
            }
            
            xpos = (Float) valueBinding(context, "xpos", xpos);
            ypos = (Float) valueBinding(context, "ypos", ypos);
            if (xpos != null || ypos != null) {
                image.setAbsolutePosition(xpos, ypos);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);           
        }
    }

    @Override
    public void removeITextObject()
    {
        image = null;
    }

    @Override
    public void handleAdd(Object other)
    {
        throw new RuntimeException("can't add " + other.getClass().getName() + " to barcode");
    }
}

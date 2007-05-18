package org.jboss.seam.pdf.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.imageio.ImageIO;

import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Image.Type;
import org.jboss.seam.pdf.ui.ITextComponent;
import org.jboss.seam.ui.graphicImage.GraphicImageResource;
import org.jboss.seam.ui.graphicImage.GraphicImageStore;
import org.jboss.seam.ui.graphicImage.GraphicImageStore.ImageWrapper;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;


/**
 * original implementation from JBSEAM-1155 by ivan
 */

public class UIBarCode 
    extends ITextComponent
{
    
    private Barcode barcode;
    private Image image;
    
    
    private String type;
    private String code;
    private Float xpos;
    private Float ypos;
    private Float rotDegrees;
    private String altText;
    private Float barHeight;
    private Float textSize;
    private Float minBarWidth;
    private Float barMultiplier;
    


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getRotDegrees(){
        return rotDegrees;
    }

    public void setRotDegrees(Float rotDegrees) {
        this.rotDegrees = rotDegrees;
    }
    
    public Float getXpos() {
        return xpos;
    }

    public void setXpos(Float xpos) {
        this.xpos = xpos;
    }

    public Float getYpos() {
        return ypos;
    }

    public void setYpos(Float ypos) {
        this.ypos = ypos;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Float getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(Float barHeight) {
        this.barHeight = barHeight;
    }

    public Float getBarMultiplier() {
        return barMultiplier;
    }

    public void setBarMultiplier(Float barMultiplier) {
        this.barMultiplier = barMultiplier;
    }

    public Float getMinBarWidth() {
        return minBarWidth;
    }

    public void setMinBarWidth(Float minBarWidth) {
        this.minBarWidth = minBarWidth;
    }

    public Float getTextSize() {
        return textSize;
    }

    public void setTextSize(Float textSize) {
        this.textSize = textSize;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state)
    {
       Object[] values = (Object[]) state;
       super.restoreState(context, values[0]);
       
       type = (String) values[1];
       code = (String) values[2];
       xpos = (Float) values[5];
       ypos = (Float) values[6];
       rotDegrees = (Float) values[7];
       altText = (String) values[8];
       barHeight = (Float) values[9];
       textSize      = (Float) values[10];
       minBarWidth   = (Float) values[11];
       barMultiplier = (Float) values[12];
    }

    @Override
    public Object saveState(FacesContext context)
    {
       Object[] values = new Object[13];
       
       values[0] = super.saveState(context);
       values[1] = type;
       values[2] = code;
       values[5] = xpos;
       values[6] = ypos;
       values[7] = rotDegrees;
       values[8] = altText;
       values[9] = barHeight;
       values[10] = textSize;
       values[11] = minBarWidth;
       values[12] = barMultiplier;
       
       return values;
    }
    
    
    @Override
    public Object getITextObject() {
        return image;
    }

    @Override
    public void createITextObject(FacesContext context) {
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
        if (minBarWidth != null) {
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
    public void removeITextObject() {
        image = null;
    }

    @Override
    public void handleAdd(Object other) {
        throw new RuntimeException("can't add " + other.getClass().getName() + " to barcode");
    }
    
    
    
    
    public static byte[] imageToByteArray(java.awt.Image image) 
        throws IOException 
    {       
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), 
                                                        image.getHeight(null), 
                                                        BufferedImage.TYPE_INT_RGB);
        Graphics gc = bufferedImage.createGraphics();
        gc.drawImage(image, 0, 0, null);
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", stream);
   
        return stream.toByteArray();
  }
    
    @Override
    public void noITextParentFound() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();        
            ResponseWriter response = context.getResponseWriter();
            response.startElement("img", null);
            GraphicImageStore store = GraphicImageStore.instance();

            
            byte[] imageData = imageToByteArray(barcode.createAwtImage(Color.BLACK, Color.WHITE));
          
            
            String key = store.put(new ImageWrapper(imageData, Type.IMAGE_JPEG));
            String url = context.getExternalContext().getRequestContextPath() +
            GraphicImageResource.GRAPHIC_IMAGE_RESOURCE_PATH + "/" + key + Type.IMAGE_JPEG.getExtension();

            response.writeAttribute("src", url, null);

            response.endElement("img");

            Manager.instance().beforeRedirect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}

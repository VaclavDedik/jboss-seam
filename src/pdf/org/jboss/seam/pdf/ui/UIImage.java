package org.jboss.seam.pdf.ui;

import org.jboss.seam.pdf.ITextUtils;

import javax.faces.event.*;
import javax.faces.context.*;
import javax.faces.component.*;
import javax.servlet.http.*;

import java.io.*;
import java.net.URL;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class UIImage
    extends ITextComponent
{
    public static final String COMPONENT_TYPE   = "org.jboss.seam.pdf.ui.UIImage";

    Image image;
    
    String resource;
    float  rotation;
    float  height;
    float  width;
    String alignment;
    String alt;
    
    Boolean wrap;
    Boolean underlying;

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setWrap(Boolean wrap) {
        this.wrap = wrap;
    }

    public void setUnderlying(Boolean underlying) {
        this.underlying = underlying;
    }


    public Object getITextObject() {
        return image;
    }
   
    public void removeITextObject() {
        image = null;
    }

    public void createITextObject() {
        
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url == null) {
            throw new RuntimeException("cannot locate image resource " + resource);
        }
        try {
            image = Image.getInstance(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (rotation != 0) {
            image.setRotationDegrees(rotation);
        }

        if (height>0 || width > 0) {
            image.scaleAbsolute(width, height);
        }

        int alignmentValue =0;
        if (alignment != null) {
            alignmentValue = (ITextUtils.alignmentValue(alignment));
        }
        if (wrap!=null && wrap.booleanValue()) {
            alignmentValue |= Image.TEXTWRAP;
        } 
        if (underlying!= null && underlying.booleanValue()) {
            alignmentValue |= Image.UNDERLYING;
        }

        image.setAlignment(alignmentValue);

        if (alt != null) {
            image.setAlt(alt);
        }
    }

    public void add(Object o) {
        throw new RuntimeException("can't add " + o.getClass().getName() + " to image");
        //image.add(o);
    }
}

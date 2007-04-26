package org.jboss.seam.pdf.ui;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

import com.lowagie.text.*;

public abstract class UIChart 
    extends ITextComponent 
{
    Image image = null;
    float height = 300;
    float width  = 400;

    public void setHeight(float height) {
        this.height = height;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public abstract JFreeChart getChart(FacesContext context);
    
    @Override
    public void createITextObject(FacesContext context) {
        JFreeChart chart = getChart(context);
               
        height = (Float) valueBinding(context, "height", height);
        width =  (Float) valueBinding(context, "width", width);
        
        org.jboss.seam.core.Image seamImage = new org.jboss.seam.core.Image();  
        seamImage.setBufferedImage(chart.createBufferedImage((int)width,(int)height));
        
        try {
            image = Image.getInstance(seamImage.getImage());           
        } catch (Exception e) {
            throw new RuntimeException(e);            
        }

    }
    
    @Override
    public void encodeBegin(FacesContext context) 
        throws IOException
    {       
        // bypass super to avoid createITextObject()
        createDataset();
    }
    
    
    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        // call create here so that we'll have a valid chart
        createITextObject(context);
        super.encodeEnd(context);
    }

    @Override
    public Object getITextObject() {
        return image;
    }

    @Override
    public void handleAdd(Object arg0) {
        throw new RuntimeException("No children allowed");
    }

    @Override
    public void removeITextObject() {
        image = null;
    }

    
    public abstract void createDataset();
    public abstract Dataset getDataset();


}

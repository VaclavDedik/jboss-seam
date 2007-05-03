package org.jboss.seam.pdf.ui;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.pdf.ITextUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;

import com.lowagie.text.*;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public abstract class UIChart 
    extends ITextComponent 
{
    private Image image = null;
    private JFreeChart chart = null;
    
    float height = 300;
    float width  = 400;    
    
    String borderBackgroundPaint;
    String borderPaint;
    String borderStroke;
    boolean borderVisible = true;
    
    String plotBackgroundPaint;
    Float  plotBackgroundAlpha;
    Float  plotForegroundAlpha;
    String plotOutlineStroke; 
    String plotOutlinePaint;
    
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public void setBorderBackgroundPaint(String backgroundPaint) {
        this.borderBackgroundPaint = backgroundPaint;
    }
    
    public String getBorderBackgroundPaint() {
        return (String) valueBinding(FacesContext.getCurrentInstance(), "backgroundPaint", borderBackgroundPaint);
    }
    
    public void setBorderPaint(String borderPaint) {
        this.borderPaint = borderPaint;
    }
    
    public String getBorderPaint() {
        return (String) valueBinding(FacesContext.getCurrentInstance(), "borderPaint", borderPaint);
    }
    
    public void setBorderStroke(String borderStroke) {
        this.borderStroke = borderStroke;
    }
    
    public String getBorderStroke() {
        return (String) valueBinding(FacesContext.getCurrentInstance(), "borderStroke", borderStroke);
    }
    
    public void setBorderVisible(boolean borderVisible) {
        this.borderVisible = borderVisible;
    }
    
    public boolean getBorderVisible() {
        return (Boolean) valueBinding(FacesContext.getCurrentInstance(), "borderVisible", borderVisible);
    }
    

    public void setPlotBackgroundAlpha(Float plotBackgroundAlpha) {
        this.plotBackgroundAlpha = plotBackgroundAlpha;
    }

    public Float getPlotBackgroundAlpha() {
        return (Float) valueBinding(FacesContext.getCurrentInstance(), "plotBackgroundAlpha", plotBackgroundAlpha);     
    }
   
    public void setPlotBackgroundPaint(String plotBackgroundPaint) {
        this.plotBackgroundPaint = plotBackgroundPaint;
    }
    
    public String getPlotBackgroundPaint() {
        return (String) valueBinding(FacesContext.getCurrentInstance(), "plotBackgroundPaint", plotBackgroundPaint);
    }

    public void setPlotForegroundAlpha(Float plotForegroundAlpha) {
        this.plotForegroundAlpha = plotForegroundAlpha;
    }
    public Float getPlotForegroundAlpha() {
        return (Float) valueBinding(FacesContext.getCurrentInstance(), "plotForegroundAlpha", plotForegroundAlpha);
    }

    public void setPlotOutlinePaint(String plotOutlinePaint) {
        this.plotOutlinePaint = plotOutlinePaint;
    }
    
    public String getPlotOutlinePaint() {
        return (String) valueBinding(FacesContext.getCurrentInstance(), "plotOutlinePaint", plotOutlinePaint);    
    }
    
    public void setPlotOutlineStroke(String plotOutlineStroke) {
        this.plotOutlineStroke = plotOutlineStroke;
    }
    
    public String getPlotOutlineStroke() {
        return (String) valueBinding(FacesContext.getCurrentInstance(), "plotOutlineStroke", plotOutlineStroke);
    }   
    
    public static Paint findColor(String name) {
        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent(name);
        
        if (component != null) {
            if (component instanceof UIColor) {                
                return ((UIColor) component).getPaint();
            } else {
                throw new RuntimeException();                
            }            
        }
        
        return ITextUtils.colorValue(name);        
    }
    
    public static Stroke findStroke(String id) {
        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent(id);

        if (component instanceof UIStroke) {

            return ((UIStroke) component).getStroke();
        } else {
            throw new RuntimeException();                

        }
    }
    
    public abstract JFreeChart createChart(FacesContext context);
    public JFreeChart getChart() {
        return chart;
    }
        
    @Override
    public void createITextObject(FacesContext context) {                        
        if (borderBackgroundPaint != null) {
            chart.setBackgroundPaint(findColor(getBorderBackgroundPaint()));
        }
        
        if (borderPaint != null) {
            chart.setBorderPaint(findColor(getBorderPaint()));
        }

        if (borderStroke != null) {
            chart.setBorderStroke(findStroke(getBorderStroke()));
        }

        chart.setBorderVisible(getBorderVisible());      
        
        configurePlot(chart.getPlot());   
        
        height = (Float) valueBinding(context, "height", height);
        width =  (Float) valueBinding(context, "width", width);        
        
        try { 
            UIDocument doc = (UIDocument) findITextParent(getParent(), UIDocument.class);
            PdfWriter writer = (PdfWriter) doc.getWriter();
            PdfContentByte cb = writer.getDirectContent(); 
            PdfTemplate tp = cb.createTemplate(width, height); 
            Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());             
            chart.draw(g2, new Rectangle2D.Double(0, 0, width, height)); 
            g2.dispose(); 
            
            image = new ImgTemplate(tp);
        } catch (Exception e) {             
            throw new RuntimeException(e);
        } 
    }   
    
   

    public void configurePlot(Plot plot) {   
        if (plotBackgroundAlpha != null) {
            plot.setBackgroundAlpha(plotBackgroundAlpha);
        }
        
        if (plotForegroundAlpha != null) {
            plot.setForegroundAlpha(plotForegroundAlpha);          
        }
        
        if (plotBackgroundPaint != null) {
            plot.setBackgroundPaint(findColor(plotBackgroundPaint));
        }
        
        if (plotOutlinePaint != null) {
            plot.setOutlinePaint(findColor(plotOutlinePaint));
        }
        
        if (plotOutlineStroke != null) { 
            plot.setOutlineStroke(findStroke(plotOutlineStroke));
        }        
    }
    
    
            
    @Override
    public void encodeBegin(FacesContext context) 
        throws IOException
    {               
        // bypass super to avoid createITextObject() before the chart is ready        
        createDataset();
        chart = createChart(context);
        
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
        chart = null;
    }

    
    public abstract void createDataset();
    public abstract Dataset getDataset();
}

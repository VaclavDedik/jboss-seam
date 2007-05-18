package org.jboss.seam.pdf.ui;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;

public class UIChartData 
    extends ITextComponent 
{ 
    private String key;
    private String columnKey;
    private String rowKey;
    private Number value;
    private Float explodedPercent;
    
    private String sectionPaint;
    private String sectionOutlinePaint;
    private String sectionOutlineStroke;
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }
    
    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public void setValue(Double value) {        
        this.value = value;
    }

    public Float getExplodedPercent() {
        return (Float) valueBinding("explodedPercent", explodedPercent);
    }

    public void setExplodedPercent(Float explodedPercent) {
        this.explodedPercent = explodedPercent;
    }
    

    public String getSectionOutlinePaint() {
        return (String) valueBinding("sectionOutlinePaint", sectionOutlinePaint);
    }

    public void setSectionOutlinePaint(String sectionOutlinePaint) {
        this.sectionOutlinePaint = sectionOutlinePaint;
    }

    public String getSectionOutlineStroke() {
        return (String) valueBinding("sectionOutlineStroke", sectionOutlineStroke);
    }

    public void setSectionOutlineStroke(String sectionOutlineStroke) {
        this.sectionOutlineStroke = sectionOutlineStroke;
    }

    public String getSectionPaint() {
        return (String) valueBinding("sectionPaint", sectionPaint);
    }

    public void setSectionPaint(String sectionPaint) {
        this.sectionPaint = sectionPaint;
    }

    
    @Override
    public void restoreState(FacesContext context, Object state)
    {
       Object[] values = (Object[]) state;
       super.restoreState(context, values[0]);
      
       key                  = (String) values[1];
       columnKey            = (String) values[2];
       rowKey               = (String) values[3];
       value                = (Number) values[4];
       explodedPercent      = (Float)  values[5];
       sectionPaint         = (String) values[6];
       sectionOutlinePaint  = (String) values[7];
       sectionOutlineStroke = (String) values[8];
    }

    @Override
    public Object saveState(FacesContext context)
    {
       Object[] values = new Object[8];

       values[1] = key;
       values[2] = columnKey;
       values[3] = rowKey;
       values[4] = explodedPercent;
       values[5] = sectionPaint;
       values[6] = sectionOutlinePaint;
       values[7] = sectionOutlineStroke;
       
       return values;
    }
    
    
    @Override
    public void encodeEnd(FacesContext context) 
        throws IOException
    {
        super.encodeEnd(context);
        
        key = (String) valueBinding("key", key);
        rowKey = (String) valueBinding("rowkey", rowKey);
        columnKey = (String) valueBinding("columnKey", columnKey);
        value = (Number) valueBinding("value", value);
        
        UIChart chart = (UIChart) findITextParent(getParent(), UIChart.class);
        if (chart != null) {            
            Dataset dataset = chart.getDataset();
            
            if (dataset instanceof DefaultPieDataset) {
                DefaultPieDataset piedata = (DefaultPieDataset) dataset;
                piedata.setValue(key, value);               

                PiePlot plot = (PiePlot) chart.getChart().getPlot();
                
                if (explodedPercent != null) {
                    plot.setExplodePercent(key, getExplodedPercent());
                }
                
                if (sectionPaint != null) {                    
                    plot.setSectionPaint(key, UIChart.findColor(getSectionPaint()));
                }
                
                if (sectionOutlinePaint != null) {
                    plot.setSectionOutlinePaint(key, UIChart.findColor(getSectionOutlinePaint()));
                }
                
                if (sectionOutlineStroke != null) {
                    plot.setSectionOutlineStroke(key, UIChart.findStroke(getSectionOutlineStroke()));
                }                
            } else if (dataset instanceof DefaultCategoryDataset) {
                DefaultCategoryDataset data = (DefaultCategoryDataset) dataset;
                
                //CategoryPlot plot = (CategoryPlot) chart.getChart().getPlot();

                if (rowKey == null) {
                    UIChartSeries series = (UIChartSeries) findITextParent(this, UIChartSeries.class);
                    rowKey = series.getKey();
                }
                data.addValue(value, rowKey, columnKey);       
            } else {
                throw new RuntimeException("Cannot add data to dataset of type " + dataset.getClass());
            }         
        }
    }
    
    @Override
    public void createITextObject(FacesContext context) {
     
    }

    @Override
    public Object getITextObject() {      
        return null;
    }

    @Override
    public void handleAdd(Object other) {
        
    }

    @Override
    public void removeITextObject() {
      
    }
}

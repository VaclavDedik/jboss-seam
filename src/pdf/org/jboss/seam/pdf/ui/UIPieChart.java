package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;

public class UIPieChart 
    extends UIChart 
{
    DefaultPieDataset data;
    
    String title;
    
    boolean legend;
    boolean tooltips;
    boolean urls;
        
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setLegend(boolean legend) {
        this.legend = legend;
    }
    
    public void setTooltips(boolean tooltips) {
        this.tooltips = tooltips;
    }
    
    public void setUrls(boolean urls) {
        this.urls = urls;
    }
    
    @Override
    public void createDataset() {
        data = new DefaultPieDataset();
    }
    
    @Override
    public Dataset getDataset() {
        return data;
    }
    
    
    
    @Override
    public JFreeChart getChart(FacesContext context) {         
        legend = (Boolean) valueBinding(context, "legend", legend);
        tooltips = (Boolean) valueBinding(context, "tooltips", tooltips);
        urls = (Boolean) valueBinding(context, "urls", urls);
        
        return ChartFactory.createPieChart(title, data, legend, tooltips, urls);
    }

    public void restoreState(FacesContext context, Object state) {
        super.restoreState(context, state);        
    }

    public Object saveState(FacesContext context) {
        return super.saveState(context);
    }
}

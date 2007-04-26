package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

public class UIPieChart3D 
    extends UIPieChart 
{
    @Override
    public JFreeChart getChart(FacesContext context) {         
        legend = (Boolean) valueBinding(context, "legend", legend);
        tooltips = (Boolean) valueBinding(context, "tooltips", tooltips);
        urls = (Boolean) valueBinding(context, "urls", urls);
        
        return ChartFactory.createPieChart3D(title, data, legend, tooltips, urls);
    }
}

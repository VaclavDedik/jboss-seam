package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

public class UILineChart
    extends UIChart 
{
    String title;
    String domainAxisLabel;
    String rangeAxisLabel;
    CategoryDataset dataset;
    String orientation;
    
    boolean legend;
    boolean tooltips;
    boolean urls;
    boolean is3D = false;
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    public void setDomainAxisLabel(String domainAxisLabel) {
        this.domainAxisLabel = domainAxisLabel;
    }

    public void setRangeAxisLabel(String rangeAxisLabel) {
        this.rangeAxisLabel = rangeAxisLabel;
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
    
    public void setIs3D(boolean is3D) {
        this.is3D = true;
    }
    
    @Override
    public void createDataset() {
        dataset = new DefaultCategoryDataset();
    }

    @Override
    public JFreeChart getChart(FacesContext context) {
        if (!is3D) {
            return ChartFactory.createLineChart(title,
                    domainAxisLabel,
                    rangeAxisLabel,
                    dataset,
                    plotOrientation(orientation),
                    legend,
                    tooltips,
                    urls);
        } else {
            return ChartFactory.createLineChart3D(title,
                    domainAxisLabel,
                    rangeAxisLabel,
                    dataset,
                    plotOrientation(orientation),
                    legend,
                    tooltips,
                    urls);
        }
    }

    @Override
    public Dataset getDataset() {        
        return dataset;
    }

    public PlotOrientation plotOrientation(String orientation) {
        if (orientation != null && orientation.equalsIgnoreCase("horizontal")) {
            return PlotOrientation.HORIZONTAL;
        } else {
            return PlotOrientation.VERTICAL;
        }
    }

}

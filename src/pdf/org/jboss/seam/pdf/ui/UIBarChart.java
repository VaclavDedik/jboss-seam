package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

public class UIBarChart 
    extends UICategoryChartBase
{
    private CategoryDataset dataset;
    
    private String title;
    private String categoryAxisLabel;
    private String valueAxisLabel;
       
    private String orientation;
    
    private boolean legend;
    private boolean is3D = false;

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return (String) valueBinding("title", title);
    }
    
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    public String getOrientation() {
        return (String) valueBinding("orientation", orientation);
    }
    
    public void setCategoryAxisLabel(String categoryAxisLabel) {
        this.categoryAxisLabel = categoryAxisLabel;
    }
    
    public String getCategoryAxisLabel() {
        return (String) valueBinding("categoryAxisLabel", categoryAxisLabel);
    }

    public void setValueAxisLabel(String valueAxisLabel) {
        this.valueAxisLabel = valueAxisLabel;
    }
    
    public String getValueAxisLabel() {
        return (String) valueBinding("valueAxisLabel", valueAxisLabel);
    }

    public void setLegend(boolean legend) {
        this.legend = legend;
    }
    
    public boolean getLegend() {
        return (Boolean) valueBinding("legend", legend);
    }

    public void setIs3D(boolean is3D) {
        this.is3D = true;
    }
       
    @Override
    public void createDataset() {
        dataset = new DefaultCategoryDataset();
    }
    


//    @Override
//    public void configurePlot(Plot p) {
//        super.configurePlot(p);
//    }

    
    @Override
    public void configureRenderer(CategoryItemRenderer renderer){
        super.configureRenderer(renderer);
        if (renderer instanceof BarRenderer) { 
            configureRenderer((BarRenderer) renderer);
        }
    }
    
    
    public void configureRenderer(BarRenderer renderer) {
           
    }

    @Override
    public JFreeChart createChart(FacesContext context) {        
        if (!is3D) {
            return ChartFactory.createBarChart(getTitle(),
                    getCategoryAxisLabel(),
                    getValueAxisLabel(),
                    dataset,
                    plotOrientation(orientation),
                    getLegend(),
                    false,
                    false);
        } else {
            return ChartFactory.createBarChart3D(title,
                    getCategoryAxisLabel(),
                    getValueAxisLabel(),
                    dataset,
                    plotOrientation(orientation),
                    getLegend(),
                    false,
                    false);
        }
    }

    @Override
    public Dataset getDataset() {        
        return dataset;
    }





}

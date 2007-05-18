package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

public class UILineChart
    extends UICategoryChartBase 
{
    CategoryDataset dataset;

    String title;
    String domainAxisLabel;
    String rangeAxisLabel;
    String orientation;
    
    boolean legend;
    boolean is3D = false;
       
    public String getTitle() {
        return (String) valueBinding("title", title);
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getOrientation() {
        return (String) valueBinding("orientation", orientation);
    }
    
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    public String getDomainAxisLabel() {
        return (String) valueBinding("domainAxisLabel", domainAxisLabel);
    }
    
    public void setDomainAxisLabel(String domainAxisLabel) {
        this.domainAxisLabel = domainAxisLabel;
    }

    
    public String getRangeAxisLabel() {
        return (String) valueBinding("rangeAxisLabel", rangeAxisLabel);
    }
    
    public void setRangeAxisLabel(String rangeAxisLabel) {
        this.rangeAxisLabel = rangeAxisLabel;
    }   

    public boolean getLegend() {
        return (Boolean) valueBinding("legend", legend);
    }
    
    public void setLegend(boolean legend) {
        this.legend = legend;
    }    
      
    public boolean getIs3D() {
        return (Boolean) valueBinding("is3D", is3D);
    }
    
    public void setIs3D(boolean is3D) {
        this.is3D = is3D;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state)
    {
       Object[] values = (Object[]) state;
       super.restoreState(context, values[0]);      
       
       title           = (String) values[1];
       domainAxisLabel = (String) values[2];
       rangeAxisLabel  = (String) values[3];
       orientation     = (String) values[4];
       legend          = (Boolean) values[5];
       is3D            = (Boolean) values[6];
    }

    @Override
    public Object saveState(FacesContext context)
    {
       Object[] values = new Object[6];

       values[1] = title;
       values[2] = domainAxisLabel;
       values[3] = rangeAxisLabel;
       values[4] = orientation;
       values[5] = legend;
       values[6] = is3D;

       return values;
    }
    
    @Override
    public void createDataset() {
        dataset = new DefaultCategoryDataset();
    }

    @Override
    public JFreeChart createChart(FacesContext context) {
        if (!is3D) {
            return ChartFactory.createLineChart(title,
                    getDomainAxisLabel(),
                    getRangeAxisLabel(),
                    dataset,
                    plotOrientation(getOrientation()),
                    getLegend(),
                    false,
                    false);
        } else {
            return ChartFactory.createLineChart3D(title,
                    getDomainAxisLabel(),
                    getRangeAxisLabel(),
                    dataset,
                    plotOrientation(getOrientation()),
                    getLegend(),
                    false,
                    false);
        }
    }

//    @Override
//    public void configurePlot(Plot p) {
//        super.configurePlot(p);
//    }
    
    
    @Override
    public void configureRenderer(CategoryItemRenderer renderer){
        super.configureRenderer(renderer);
        //System.out.println("UILINECHART RENDERER IS " + renderer);
    }
    
    @Override
    public Dataset getDataset() {        
        return dataset;
    }

}

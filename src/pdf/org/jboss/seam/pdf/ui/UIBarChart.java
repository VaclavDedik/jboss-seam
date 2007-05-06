package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

public class UIBarChart 
    extends UIChart 
{
    private CategoryDataset dataset;
    
    private String title;
    private String categoryAxisLabel;
    private String valueAxisLabel;
       
    private String orientation;
    
    private boolean legend;
    private boolean is3D = false;

    private Boolean domainGridlinesVisible;
    private String domainGridlinePaint;
    private String domainGridlineStroke;
    private Boolean rangeGridlinesVisible;
    private String rangeGridlinePaint;
    private String rangeGridlineStroke;      
    
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
          
    public String getDomainGridlinePaint() {
        return (String) valueBinding("domainGridlinePaint", domainGridlinePaint);
    }

    public void setDomainGridlinePaint(String domainGridlinePaint) {
        this.domainGridlinePaint = domainGridlinePaint;
    }

    public String getDomainGridlineStroke() {
        return (String) valueBinding("domainGridlineStroke", domainGridlineStroke);
    }

    public void setDomainGridlineStroke(String domainGridlineStroke) {
        this.domainGridlineStroke = domainGridlineStroke;
    }

    public Boolean getDomainGridlinesVisible() {
        return (Boolean) valueBinding("domainGridlinesVisible", domainGridlinesVisible);
    }

    public void setDomainGridlinesVisible(Boolean domainGridlinesVisible) {
        this.domainGridlinesVisible = domainGridlinesVisible;
    }

    public String getRangeGridlinePaint() {
        return (String) valueBinding("rangeGridlinePaint", rangeGridlinePaint);
    }

    public void setRangeGridlinePaint(String rangeGridlinePaint) {
        this.rangeGridlinePaint = rangeGridlinePaint;
    }

    public String getRangeGridlineStroke() {
        return (String) valueBinding("rangeGridlineStroke", rangeGridlineStroke);
    }

    public void setRangeGridlineStroke(String rangeGridlineStroke) {
        this.rangeGridlineStroke = rangeGridlineStroke;
    }

    public Boolean getRangeGridlinesVisible() {
        return (Boolean) valueBinding("rangeGridlinesVisible", rangeGridlinesVisible);
    }

    public void setRangeGridlinesVisible(Boolean rangeGridlinesVisible) {
        this.rangeGridlinesVisible = rangeGridlinesVisible;
    }
    
    @Override
    public void createDataset() {
        dataset = new DefaultCategoryDataset();
    }
    
    @Override
    public void configurePlot(Plot p) {
        super.configurePlot(p);
        CategoryPlot plot = (CategoryPlot) p;

        //plot.setAxisOffset(RectangleInsets)
        //plot.setDomainAxisLocation(arg0);
        //plot.setRangeAxisLocation(arg0);
        
        if (domainGridlinesVisible != null) { 
            plot.setDomainGridlinesVisible(domainGridlinesVisible);
        }
        //plot.setDomainGridlinePosition(CategoryAnchor)
        if (domainGridlinePaint != null) {
            plot.setDomainGridlinePaint(findColor(domainGridlinePaint));
        }
        if (domainGridlineStroke != null) {
            plot.setDomainGridlineStroke(findStroke(domainGridlineStroke));
        }
        
        if (rangeGridlinesVisible != null) {
        plot.setRangeGridlinesVisible(rangeGridlinesVisible);
        }
        //plot.setRangeGridlinePosition(CategoryAnchor)
        if (rangeGridlinePaint!=null) {        
            plot.setRangeGridlinePaint(findColor(rangeGridlinePaint));
        }
        if (rangeGridlineStroke!=null) {
            plot.setRangeGridlineStroke(findStroke(rangeGridlineStroke));
        }
                        
        configureRenderer((BarRenderer) plot.getRenderer());
              
    }
    
    public void configureRenderer(BarRenderer renderer) {
        //renderer.setItemMargin(0.0);
                
//        renderer.setBase(arg0);
//        renderer.setBaseFillPaint(arg0);
//        renderer.setBaseItemLabelFont(arg0);
//        renderer.setBaseItemLabelPaint(arg0);
//        renderer.setBaseItemLabelsVisible(arg0);
//        renderer.setBaseOutlinePaint(arg0);
//        renderer.setBaseOutlineStroke(arg0);
//        renderer.setBaseSeriesVisible(arg0);
//        renderer.setBaseSeriesVisibleInLegend(arg0);
//        renderer.setBaseShape(arg0);
//        renderer.setBaseStroke();
//        renderer.setFillPaint(arg0);
//        renderer.setItemLabelFont(arg0);
//        renderer.setItemLabelPaint(arg0);
//        renderer.setItemLabelsVisible(arg0);
//        renderer.setItemMargin(arg0);
//        renderer.setOutlinePaint(arg0)
//        renderer.setOutlineStroke(arg0)
//        renderer.setPaint(arg0);
//        renderer.setStroke(arg0);
        
        
        
        //renderer.setBaseOutlineStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, 
                //new float[] {10,3}, 0));        

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

    public PlotOrientation plotOrientation(String orientation) {
        if (orientation != null && orientation.equalsIgnoreCase("horizontal")) {
            return PlotOrientation.HORIZONTAL;
        } else {
            return PlotOrientation.VERTICAL;
        }
    }



}

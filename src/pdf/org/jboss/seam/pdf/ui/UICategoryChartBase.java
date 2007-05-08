package org.jboss.seam.pdf.ui;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;

public abstract class UICategoryChartBase 
    extends UIChart 
{
    private Boolean domainGridlinesVisible;
    private String domainGridlinePaint;
    private String domainGridlineStroke;
    private Boolean rangeGridlinesVisible;
    private String rangeGridlinePaint;
    private String rangeGridlineStroke;      
    
    
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
    public void configurePlot(Plot plot) {
        super.configurePlot(plot);
        if (plot instanceof CategoryPlot) {
            configurePlot((CategoryPlot) plot);
        } else {
            System.out.println("UICATEGORYCHART --- unknown plot " + plot);
        }
    }
    
    public void configurePlot(CategoryPlot plot) {
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
                        
        configureRenderer(plot.getRenderer());
    }
    
    
    public void configureRenderer(CategoryItemRenderer renderer){
        //renderer.setItemMargin(0.0);
        
//      renderer.setBase(arg0);
//      renderer.setBaseFillPaint(arg0);
//      renderer.setBaseItemLabelFont(arg0);
//      renderer.setBaseItemLabelPaint(arg0);
//      renderer.setBaseItemLabelsVisible(arg0);
//      renderer.setBaseOutlinePaint(arg0);
//      renderer.setBaseOutlineStroke(arg0);
//      renderer.setBaseSeriesVisible(arg0);
//      renderer.setBaseSeriesVisibleInLegend(arg0);
//      renderer.setBaseShape(arg0);
//      renderer.setBaseStroke();
//      renderer.setFillPaint(arg0);
//      renderer.setItemLabelFont(arg0);
//      renderer.setItemLabelPaint(arg0);
//      renderer.setItemLabelsVisible(arg0);
//      renderer.setItemMargin(arg0);
//      renderer.setOutlinePaint(arg0)
//      renderer.setOutlineStroke(arg0)
//      renderer.setPaint(arg0);
//      renderer.setStroke(arg0);
      
      
      
      //renderer.setBaseOutlineStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, 
              //new float[] {10,3}, 0));     
    }

}

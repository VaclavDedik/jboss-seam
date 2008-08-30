package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;

public abstract class UICategoryChartBase extends UIChart
{
   private String orientation;

   private boolean legend;
   private boolean is3D = false;

   private String title;
   private String titleBackgroundPaint;
   private String titlePaint;

   private String legendBackgroundPaint;
   private String legendItemPaint;

   private String domainAxisLabel;
   private String domainAxisPaint;
   private Boolean domainGridlinesVisible;
   private String domainGridlinePaint;
   private String domainGridlineStroke;

   private String rangeAxisLabel;
   private String rangeAxisPaint;
   private Boolean rangeGridlinesVisible;
   private String rangeGridlinePaint;
   private String rangeGridlineStroke;

   public String getDomainAxisLabel()
   {
      return (String) valueBinding("domainAxisLabel", domainAxisLabel);
   }

   public void setDomainAxisLabel(String categoryAxisLabel)
   {
      this.domainAxisLabel = categoryAxisLabel;
   }

   public String getRangeAxisLabel()
   {
      return (String) valueBinding("rangeAxisLabel", rangeAxisLabel);
   }

   public void setRangeAxisLabel(String valueAxisLabel)
   {
      this.rangeAxisLabel = valueAxisLabel;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getTitle()
   {
      return (String) valueBinding("title", title);
   }

   public void setOrientation(String orientation)
   {
      this.orientation = orientation;
   }

   public String getOrientation()
   {
      return (String) valueBinding("orientation", orientation);
   }

   public void setLegend(boolean legend)
   {
      this.legend = legend;
   }

   public boolean getLegend()
   {
      return (Boolean) valueBinding("legend", legend);
   }

   public void setIs3D(boolean is3D)
   {
      this.is3D = true;
   }

   public boolean getIs3D()
   {
      return (Boolean) valueBinding("is3D", is3D);
   }

   public void setTitleBackgroundPaint(String titleBackgroundPaint)
   {
      this.titleBackgroundPaint = titleBackgroundPaint;
   }

   public String getTitleBackgroundPaint()
   {
      return (String) valueBinding("titleBackgroundPaint", titleBackgroundPaint);
   }

   public void setTitlePaint(String titlePaint)
   {
      this.titlePaint = titlePaint;
   }

   public String getTitlePaint()
   {
      return (String) valueBinding("titlePaint", titlePaint);
   }

   public String getLegendBackgroundPaint()
   {
      return (String) valueBinding("legendBackgroundPaint", legendBackgroundPaint);
   }

   public void setLegendBackgroundPaint(String legendBackgroundPaint)
   {
      this.legendBackgroundPaint = legendBackgroundPaint;
   }

   public String getLegendItemPaint()
   {
      return (String) valueBinding("legendItemPaint", legendItemPaint);
   }

   public void setLegendItemPaint(String legendItemPaint)
   {
      this.legendItemPaint = legendItemPaint;
   }

   public String getDomainGridlinePaint()
   {
      return (String) valueBinding("domainGridlinePaint", domainGridlinePaint);
   }

   public void setDomainGridlinePaint(String domainGridlinePaint)
   {
      this.domainGridlinePaint = domainGridlinePaint;
   }

   public String getDomainGridlineStroke()
   {
      return (String) valueBinding("domainGridlineStroke", domainGridlineStroke);
   }

   public void setDomainGridlineStroke(String domainGridlineStroke)
   {
      this.domainGridlineStroke = domainGridlineStroke;
   }

   public Boolean getDomainGridlinesVisible()
   {
      return (Boolean) valueBinding("domainGridlinesVisible", domainGridlinesVisible);
   }

   public void setDomainGridlinesVisible(Boolean domainGridlinesVisible)
   {
      this.domainGridlinesVisible = domainGridlinesVisible;
   }

   public String getRangeGridlinePaint()
   {
      return (String) valueBinding("rangeGridlinePaint", rangeGridlinePaint);
   }

   public void setRangeGridlinePaint(String rangeGridlinePaint)
   {
      this.rangeGridlinePaint = rangeGridlinePaint;
   }

   public String getRangeGridlineStroke()
   {
      return (String) valueBinding("rangeGridlineStroke", rangeGridlineStroke);
   }

   public void setRangeGridlineStroke(String rangeGridlineStroke)
   {
      this.rangeGridlineStroke = rangeGridlineStroke;
   }

   public Boolean getRangeGridlinesVisible()
   {
      return (Boolean) valueBinding("rangeGridlinesVisible", rangeGridlinesVisible);
   }

   public void setRangeGridlinesVisible(Boolean rangeGridlinesVisible)
   {
      this.rangeGridlinesVisible = rangeGridlinesVisible;
   }

   public String getDomainAxisPaint()
   {
      return (String) valueBinding("domainAxisPaint", domainAxisPaint);
   }

   public void setDomainAxisPaint(String domainAxisPaint)
   {
      this.domainAxisPaint = domainAxisPaint;
   }

   public String getRangeAxisPaint()
   {
      return (String) valueBinding("rangeAxisPaint", rangeAxisPaint);
   }

   public void setRangeAxisPaint(String rangeAxisPaint)
   {
      this.rangeAxisPaint = rangeAxisPaint;
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] values = (Object[]) state;

      super.restoreState(context, values[0]);

      orientation = (String) values[1];
      legend = (Boolean) values[2];
      is3D = (Boolean) values[3];
      title = (String) values[4];
      titleBackgroundPaint = (String) values[5];
      titlePaint = (String) values[6];
      legendBackgroundPaint = (String) values[7];
      legendItemPaint = (String) values[8];
      domainAxisLabel = (String) values[9];
      domainAxisPaint = (String) values[10];
      domainGridlinesVisible = (Boolean) values[11];
      domainGridlinePaint = (String) values[12];
      domainGridlineStroke = (String) values[13];
      rangeAxisLabel = (String) values[14];
      rangeAxisPaint = (String) values[15];
      rangeGridlinesVisible = (Boolean) values[16];
      rangeGridlinePaint = (String) values[17];
      rangeGridlineStroke = (String) values[18];
   }

   @Override
   public Object saveState(FacesContext context)
   {
      Object[] values = new Object[19];
      values[0] = super.saveState(context);
      values[1] = orientation;
      values[2] = legend;
      values[3] = is3D;
      values[4] = title;
      values[5] = titleBackgroundPaint;
      values[6] = titlePaint;
      values[7] = legendBackgroundPaint;
      values[8] = legendItemPaint;
      values[9] = domainAxisLabel;
      values[10] = domainAxisPaint;
      values[11] = domainGridlinesVisible;
      values[12] = domainGridlinePaint;
      values[13] = domainGridlineStroke;
      values[14] = rangeAxisLabel;
      values[15] = rangeAxisPaint;
      values[16] = rangeGridlinesVisible;
      values[17] = rangeGridlinePaint;
      values[18] = rangeGridlineStroke;

      return values;
   }

   @Override
   public void configurePlot(Plot plot)
   {
      super.configurePlot(plot);
      if (plot instanceof CategoryPlot)
      {
         configurePlot((CategoryPlot) plot);
      }
      else
      {
         System.out.println("UICATEGORYCHART --- unknown plot " + plot);
      }
   }

   public void configurePlot(CategoryPlot plot)
   {
      // plot.setAxisOffset(RectangleInsets)
      // plot.setDomainAxisLocation(arg0);
      // plot.setRangeAxisLocation(arg0);

      if (getDomainGridlinesVisible() != null)
      {
         plot.setDomainGridlinesVisible(getDomainGridlinesVisible());
      }

      if (findColor(getDomainGridlinePaint()) != null)
      {
         plot.setDomainGridlinePaint(findColor(getDomainGridlinePaint()));
      }
      if (findStroke(getDomainGridlineStroke()) != null)
      {
         plot.setDomainGridlineStroke(findStroke(getDomainGridlineStroke()));
      }
      if (findColor(getDomainAxisPaint()) != null)
      {
         plot.getDomainAxis().setLabelPaint(findColor(getDomainAxisPaint()));
      }

      if (getRangeGridlinesVisible() != null)
      {
         plot.setRangeGridlinesVisible(getRangeGridlinesVisible());
      }
      if (findColor(getRangeGridlinePaint()) != null)
      {
         plot.setRangeGridlinePaint(findColor(getRangeGridlinePaint()));
      }
      if (findStroke(getRangeGridlineStroke()) != null)
      {
         plot.setRangeGridlineStroke(findStroke(getRangeGridlineStroke()));
      }
      if (findColor(getRangeAxisPaint()) != null)
      {
         plot.getRangeAxis().setLabelPaint(findColor(getRangeAxisPaint()));
      }
      configureRenderer(plot.getRenderer());
   }

   public void configureRenderer(CategoryItemRenderer renderer)
   {
      // renderer.setItemMargin(0.0);

      // renderer.setBase(arg0);
      // renderer.setBaseFillPaint(arg0);
      // renderer.setBaseItemLabelFont(arg0);
      // renderer.setBaseItemLabelPaint(arg0);
      // renderer.setBaseItemLabelsVisible(arg0);
      // renderer.setBaseOutlinePaint(arg0);
      // renderer.setBaseOutlineStroke(arg0);
      // renderer.setBaseSeriesVisible(arg0);
      // renderer.setBaseSeriesVisibleInLegend(arg0);
      // renderer.setBaseShape(arg0);
      // renderer.setBaseStroke();
      // renderer.setFillPaint(arg0);
      // renderer.setItemLabelFont(arg0);
      // renderer.setItemLabelPaint(arg0);
      // renderer.setItemLabelsVisible(arg0);
      // renderer.setItemMargin(arg0);
      // renderer.setOutlinePaint(arg0)
      // renderer.setOutlineStroke(arg0)
      // renderer.setPaint(arg0);
      // renderer.setStroke(arg0);

      // renderer.setBaseOutlineStroke(new BasicStroke(2f,
      // BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f,
      // new float[] {10,3}, 0));
   }

   public void configureTitle(TextTitle chartTitle)
   {
      if (chartTitle != null)
      {
         if (findColor(getTitleBackgroundPaint()) != null)
         {
            chartTitle.setBackgroundPaint(findColor(getTitleBackgroundPaint()));
         }

         if (findColor(getTitlePaint()) != null)
         {
            chartTitle.setPaint(findColor(getTitlePaint()));
         }
      }
   }

   void configureLegend(LegendTitle chartLegend)
   {
      if (chartLegend != null)
      {
         if (findColor(getLegendBackgroundPaint()) != null)
         {
            chartLegend.setBackgroundPaint(findColor(getLegendBackgroundPaint()));
         }
         if (findColor(getLegendItemPaint()) != null)
         {
            chartLegend.setItemPaint(findColor(getLegendItemPaint()));
         }
      }
   }

}

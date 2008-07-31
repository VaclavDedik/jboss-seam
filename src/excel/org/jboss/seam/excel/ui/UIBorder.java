package org.jboss.seam.excel.ui;

public class UIBorder extends ExcelComponent
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIBorder";

   private String border;
   private String lineStyle;
   private String color;

   public String getBorder()
   {
      return (String) valueOf("border", border);
   }

   public void setBorder(String border)
   {
      this.border = border;
   }

   public String getLineStyle()
   {
      return (String) valueOf("lineStyle", lineStyle);
   }

   public void setLineStyle(String lineStyle)
   {
      this.lineStyle = lineStyle;
   }

   public String getColor()
   {
      return (String) valueOf("color", color);
   }

   public void setColor(String color)
   {
      this.color = color;
   }

   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}

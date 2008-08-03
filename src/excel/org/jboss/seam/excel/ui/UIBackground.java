package org.jboss.seam.excel.ui;

public class UIBackground extends ExcelComponent
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIBackground";

   private String color;
   private String pattern;

   public String getColor()
   {
      return (String) valueOf("color", color);
   }

   public void setColor(String color)
   {
      this.color = color;
   }

   public String getPattern()
   {
      return (String) valueOf("pattern", pattern);
   }

   public void setPattern(String pattern)
   {
      this.pattern = pattern;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}

package org.jboss.seam.excel.ui;

public class UIFont extends ExcelComponent
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIFont";

   private String name;
   private String color; // TODO: support non-constant colors
   private Integer pointSize;
   private Boolean bold;
   private Boolean italic;
   private Boolean struckOut;
   private String scriptStyle;
   private String underlineStyle;

   public String getName()
   {
      return (String) valueOf("name", name);
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getColor()
   {
      return (String) valueOf("color", color);
   }

   public void setColor(String color)
   {
      this.color = color;
   }

   public Integer getPointSize()
   {
      return (Integer) valueOf("pointSize", pointSize);
   }

   public void setPointSize(Integer pointSize)
   {
      this.pointSize = pointSize;
   }

   public Boolean getBold()
   {
      return (Boolean) valueOf("bold", bold);

   }

   public void setBold(Boolean bold)
   {
      this.bold = bold;
   }

   public Boolean getItalic()
   {
      return (Boolean) valueOf("italic", italic);

   }

   public void setItalic(Boolean italic)
   {
      this.italic = italic;
   }

   public Boolean getStruckOut()
   {
      return (Boolean) valueOf("struckOut", struckOut);

   }

   public void setStruckOut(Boolean struckOut)
   {
      this.struckOut = struckOut;
   }

   public String getScriptStyle()
   {
      return (String) valueOf("scriptStyle", scriptStyle);

   }

   public void setScriptStyle(String scriptStyle)
   {
      this.scriptStyle = scriptStyle;
   }

   public String getUnderlineStyle()
   {
      return (String) valueOf("underlineStyle", underlineStyle);
   }

   public void setUnderlineStyle(String underlineStyle)
   {
      this.underlineStyle = underlineStyle;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}

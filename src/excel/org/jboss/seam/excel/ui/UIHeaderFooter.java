package org.jboss.seam.excel.ui;

public class UIHeaderFooter extends ExcelComponent
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIHeaderFooter";

   public static final String LEFT_FACET = "left";
   public static final String CENTER_FACET = "center";
   public static final String RIGHT_FACET = "right";

   public enum Type
   {
      header, footer
   }

   private Type type;

   public Type getType()
   {
      return (Type) valueOf("type", type);
   }

   public void setType(Type type)
   {
      this.type = type;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}

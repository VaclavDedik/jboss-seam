package org.jboss.seam.excel.ui;

import org.jboss.seam.excel.Command;

public class UIGroupColumns extends ExcelComponent implements Command
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIGroupColumns";

   private Integer startColumn;
   private Integer endColumn;
   private Boolean collapse;

   public Boolean getCollapse()
   {
      return (Boolean) valueOf("collapse", collapse);
   }

   public void setCollapse(Boolean collapse)
   {
      this.collapse = collapse;
   }

   public Integer getStartColumn()
   {
      return (Integer) valueOf("startColumn", startColumn);
   }

   public void setStartColumn(Integer startColumn)
   {
      this.startColumn = startColumn;
   }

   public Integer getEndColumn()
   {
      return (Integer) valueOf("endColumn", endColumn);
   }

   public void setEndColumn(Integer endColumn)
   {
      this.endColumn = endColumn;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

   public CommandType getCommandType()
   {
      return CommandType.group_columns;
   }

}

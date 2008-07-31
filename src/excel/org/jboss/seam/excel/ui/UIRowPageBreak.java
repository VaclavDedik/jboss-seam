package org.jboss.seam.excel.ui;

import org.jboss.seam.excel.Command;

public class UIRowPageBreak extends ExcelComponent implements Command
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIRowPageBreak";

   private Integer row;
   
   public Integer getRow()
   {
      return (Integer) valueOf("row", row);
   }

   public void setRow(Integer row)
   {
      this.row = row;
   }

   public CommandType getCommandType()
   {
      return CommandType.add_row_pagebreak;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}

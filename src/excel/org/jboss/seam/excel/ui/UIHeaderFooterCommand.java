package org.jboss.seam.excel.ui;

public class UIHeaderFooterCommand extends ExcelComponent
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIHeaderFooterCommand";

   public enum Command
   {
      append, date, page_number, time, total_pages, workbook_name, worksheet_name, toggle_bold, toggle_double_underline, toggle_italics, toggle_outline, toggle_shadow, toggle_strikethrough, toggle_subscript, toggle_superscript, toggle_underline, font_name, font_size
   }

   private Command command;
   private Object parameter;

   public Command getCommand()
   {
      return (Command) valueOf("command", command);
   }

   public void setCommand(Command command)
   {
      this.command = command;
   }

   public Object getParameter()
   {
      return valueOf("parameter", parameter);
   }

   public void setParameter(Object parameter)
   {
      this.parameter = parameter;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}

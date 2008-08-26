package org.jboss.seam.excel.ui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.DataModel;

import org.jboss.seam.excel.Command;
import org.jboss.seam.excel.ExcelWorkbook;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.WorksheetItem;
import org.jboss.seam.framework.Query;

public class UIWorksheet extends UIWorksheetSettings
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIWorksheet";

   private String name;
   private String var;
   private Object value;
   private Integer startRow;
   private Integer startColumn;
   private String templates;

   public Integer getStartRow()
   {
      return (Integer) valueOf("startRow", startRow);
   }

   public void setStartRow(Integer startRow)
   {
      this.startRow = startRow;
   }

   public Integer getStartColumn()
   {
      return (Integer) valueOf("startColumn", startColumn);
   }

   public void setStartColumn(Integer startColumn)
   {
      this.startColumn = startColumn;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

   public String getName()
   {
      return (String) valueOf("name", name);
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getVar()
   {
      return (String) valueOf("var", var);
   }

   public void setVar(String var)
   {
      this.var = var;
   }

   public Object getValue()
   {
      return valueOf("value", value);
   }

   public void setValue(Object value)
   {
      this.value = value;
   }

   public void setTemplates(String templates)
   {
      this.templates = templates;
   }

   public String getTemplates()
   {
      return (String) valueOf("templates", templates);
   }

   @Override
   public void encodeBegin(javax.faces.context.FacesContext facesContext) throws java.io.IOException
   {
      /**
       * Get workbook
       */
      ExcelWorkbook excelWorkbook = getWorkbook(getParent());

      if (excelWorkbook == null)
      {
         throw new ExcelWorkbookException("Could not find excel workbook");
      }

      /**
       * Create new worksheet (or select an existing one) and apply settings (if
       * any)
       */
      excelWorkbook.createOrSelectWorksheet(this);

      /**
       * Add worksheet level items
       */
      List<WorksheetItem> items = getItems(getChildren());
      for (WorksheetItem item : items)
      {
         excelWorkbook.addItem(item);
      }

      /**
       * Execute worksheet level commands
       */
      List<Command> commands = getCommands(getChildren());
      for (Command command : commands)
      {
         excelWorkbook.executeCommand(command);
      }
   }

   @SuppressWarnings("unchecked")
   public static Iterator unwrapIterator(Object value)
   {
      if (value instanceof Iterable)
      {
         return ((Iterable) value).iterator();
      }
      else if (value instanceof DataModel && ((DataModel) value).getWrappedData() instanceof Iterable)
      {
         return ((Iterable) ((DataModel) value).getWrappedData()).iterator();
      }
      else if (value instanceof Query)
      {
         return (((Query) value).getResultList()).iterator();
      }
      else if (value != null && value.getClass().isArray())
      {
         return arrayAsList(value).iterator();
      }
      else
      {
         throw new ExcelWorkbookException("A worksheet's value must be an Iterable, DataModel or Query");
      }
   }

   /**
    * Returns an iterator over objects passed to the worksheet
    * 
    * @return Iterator for values passed to the sheet
    */
   @SuppressWarnings("unchecked")
   public Iterator getDataIterator()
   {
      return unwrapIterator(getValue());
   }

   @SuppressWarnings("unchecked")
   private static List arrayAsList(Object array)
   {
      if (array.getClass().getComponentType().isPrimitive())
      {
         List list = new ArrayList();
         for (int i = 0; i < Array.getLength(array); i++)
         {
            list.add(Array.get(array, i));
         }
         return list;
      }
      else
      {
         return Arrays.asList((Object[]) array);
      }
   }

}

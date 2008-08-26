package org.jboss.seam.excel.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;

import org.jboss.seam.excel.Command;
import org.jboss.seam.excel.ExcelWorkbook;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.WorksheetItem;

public class UIColumn extends ExcelComponent
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIColumn";

   private Boolean autoSize;
   private Boolean hidden;
   private Integer width;

   public UIColumn()
   {
   }

   /**
    * Convenience constructor for settings widths through the exporter
    * 
    * @param width The column width to set
    */
   public UIColumn(Integer width)
   {
      this.width = width;
   }

   public Boolean getAutoSize()
   {
      return (Boolean) valueOf("autoSize", autoSize);
   }

   public void setAutoSize(Boolean autoSize)
   {
      this.autoSize = autoSize;
   }

   public Boolean getHidden()
   {
      return (Boolean) valueOf("hidden", hidden);
   }

   public void setHidden(Boolean hidden)
   {
      this.hidden = hidden;
   }

   public Integer getWidth()
   {
      return (Integer) valueOf("width", width);
   }

   public void setWidth(Integer width)
   {
      this.width = width;
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      /**
       * Get workbook and worksheet
       */
      ExcelWorkbook excelWorkbook = getWorkbook(getParent());

      if (excelWorkbook == null)
      {
         throw new ExcelWorkbookException("Could not find excel workbook");
      }

      /**
       * Column width etc.
       */
      excelWorkbook.applyColumnSettings(this);

      UIWorksheet sheet = (UIWorksheet) getParentByClass(getParent(), UIWorksheet.class);
      if (sheet == null)
      {
         throw new ExcelWorkbookException("Could not find worksheet");
      }

      /**
       * Add header items (if any)
       */
      // TODO: multicells
      UICell headerCell = (UICell) getFacet(HEADER_FACET);
      if (headerCell != null)
      {
         excelWorkbook.addItem(headerCell);
      }

      /**
       * Execute commands (if any)
       */
      List<Command> commands = getCommands(getChildren());
      for (Command command : commands)
      {
         excelWorkbook.executeCommand(command);
      }

      /**
       * Get UiCell template this column's data cells and iterate over sheet
       * data
       * 
       */
      for (WorksheetItem item : getItems(getChildren()))
      {
         Object oldValue = null;
         Iterator iterator = null;
         // Store away the old value for the sheet binding var (if there is one)
         if (sheet.getVar() != null) {
            oldValue = FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(sheet.getVar());
            iterator = sheet.getDataIterator();
         } else {
            // No var, no iteration...
            iterator = new ArrayList().iterator();
         }
         while (iterator.hasNext())
         {
            // Store the bound data in the request map and add the cell
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(sheet.getVar(), iterator.next());
            excelWorkbook.addItem(item);
         }

         //  No iteration, nothing to restore
         if (sheet.getVar() == null) {
            continue;
         }
         // Restore the previously modified request map (if there was a var)
         if (oldValue == null)
         {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove(sheet.getVar());
         }
         else
         {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(sheet.getVar(), oldValue);
         }
      }

      /**
       * Move column pointer to next column
       */
      excelWorkbook.nextColumn();

   }

}

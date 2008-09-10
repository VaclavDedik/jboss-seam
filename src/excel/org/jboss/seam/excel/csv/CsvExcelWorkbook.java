package org.jboss.seam.excel.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.seam.document.DocumentData;
import org.jboss.seam.document.DocumentData.DocumentType;
import org.jboss.seam.excel.ExcelWorkbook;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.WorksheetItem;
import org.jboss.seam.excel.ui.UICell;
import org.jboss.seam.excel.ui.UIColumn;
import org.jboss.seam.excel.ui.UIImage;
import org.jboss.seam.excel.ui.UILink;
import org.jboss.seam.excel.ui.UIWorkbook;
import org.jboss.seam.excel.ui.UIWorksheet;
import org.jboss.seam.excel.ui.command.Command;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * 10 minute (quite poor) implementation of csv excel workbook... Perhaps better
 * would be to use some kind of library for this.
 * 
 * Use at own risk.. :)
 * 
 * @author Daniel Roth (danielc.roth@gmail.com)
 */
public class CsvExcelWorkbook implements ExcelWorkbook
{
   private int column = 0;
   private int row = 0;
   private int sheet = -1;
   private int maxrow = 0;
   private int maxcolumn = 0;
   private int maxsheet = 0;
   
   private final String COLUMN_DELIMITER = "\"";
   private final String LINEBREAK = "\n";
   
   private Map<Integer, Map<Integer, List<String>>> table = null;
   private List<String> sheets = new ArrayList<String>();
   
   private Log log = Logging.getLog(getClass());

   public void createWorkbook(UIWorkbook uiWorkbook) throws ExcelWorkbookException
   {
      table = new TreeMap<Integer, Map<Integer, List<String>>>();

   }

   public void createOrSelectWorksheet(UIWorksheet uiWorksheet)
   {
      createOrSelectWorksheet(uiWorksheet.getName(), uiWorksheet.getStartRow(), uiWorksheet.getStartColumn());

   }

   private void createOrSelectWorksheet(String worksheetName, Integer startRow, Integer startColumn)
   {
      column = 0;
      row = 0;
      if (sheets.contains(worksheetName))
      {
         sheet = sheets.indexOf(sheets);
         column = startColumn;
         row = startRow;
      }
      else
      {
         sheet++;
         sheets.add(worksheetName);
      }

   }

   public byte[] getBytes()
   {
      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i <= maxsheet; i++)
      {
         Map<Integer, List<String>> sheet = table.get(i);
         if (sheet != null)
         {
            for (int j = 0; j <= maxrow; j++)
            {
               for (List<String> col : sheet.values())
               {
                  if (col.get(j) != null)
                     buffer.append(COLUMN_DELIMITER).append(String.valueOf(col.get(j))).append(COLUMN_DELIMITER).append(",");
               }

               buffer.append(LINEBREAK);
            }

         }
      }
      return buffer.toString().getBytes();
   }

   private void addCell(int sheet, int column, int row, UICell uiCell) throws ExcelWorkbookException
   {
      if (table.get(sheet) == null)
         table.put(sheet, new TreeMap<Integer, List<String>>());

      Map<Integer, List<String>> columns = table.get(sheet);
      if (columns.get(column) == null)
         columns.put(column, new ArrayList<String>());

      List<String> rows = columns.get(column);

      rows.add(String.valueOf(uiCell.getValue()));
      maxrow = (row > maxrow) ? row : maxrow;
      maxcolumn = (column > maxcolumn) ? column : maxcolumn;
      maxsheet = (sheet > maxsheet) ? sheet : maxsheet;

   }

   public void nextColumn()
   {
      column++;
      row = 0;
   }

   public DocumentType getDocumentType()
   {
      return new DocumentData.DocumentType("csv", "text/csv");
   }

   public void addImage(UIImage uiImage)
   {
      log.warn("addImage() is not supported by CSV exporter", new Object[0]);
   }

   public void addItem(WorksheetItem item)
   {
      UICell cell = (UICell) item;
      addCell(sheet, column, row++, cell);
   }

   public void applyWorksheetSettings(UIWorksheet uiWorksheet)
   {
      log.trace("applyWorksheetSettings() is not supported by CSV exporter", new Object[0]);
   }

   public void applyColumnSettings(UIColumn uiColumn)
   {
      log.trace("applyColumnSettings() is not supported by CSV exporter", new Object[0]);
   }

   public void executeCommand(Command command)
   {
      log.trace("executeCommand() is not supported by CSV exporter", new Object[0]);
   }

   public void addWorksheetFooter(WorksheetItem item, int colspan)
   {
      // TODO Auto-generated method stub
      
   }

   public void addWorksheetHeader(WorksheetItem item, int colspan)
   {
      // TODO Auto-generated method stub
      
   }

   public void setStylesheets(List<UILink> stylesheets)
   {
      // TODO Auto-generated method stub
      
   }

}

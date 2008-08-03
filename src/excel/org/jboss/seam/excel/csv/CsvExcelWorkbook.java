package org.jboss.seam.excel.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.excel.Command;
import org.jboss.seam.excel.DocumentData;
import org.jboss.seam.excel.ExcelWorkbook;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.Template;
import org.jboss.seam.excel.WorksheetItem;
import org.jboss.seam.excel.DocumentData.DocumentType;
import org.jboss.seam.excel.ui.UICell;
import org.jboss.seam.excel.ui.UIColumn;
import org.jboss.seam.excel.ui.UIImage;
import org.jboss.seam.excel.ui.UIWorkbook;
import org.jboss.seam.excel.ui.UIWorksheet;

/*
 * 10 minute (quite poor) implementation of csv excel workbook... 
 * Perhaps better would be to use some kind of. 
 * 
 * Use at own risk.. :)
 *   
 */
@Name("org.jboss.seam.excel.csv")
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class CsvExcelWorkbook implements ExcelWorkbook
{
   int column = 0;
   int row = 0;
   int sheet = -1;
   int maxrow = 0;
   int maxcolumn = 0;
   int maxsheet = 0;
   private Map<Integer, Map<Integer, List<String>>> table = null;
   private List<String> sheets = new ArrayList<String>();

   public void createWorkbook(UIWorkbook uiWorkbook) throws ExcelWorkbookException
   {
      table = new TreeMap<Integer, Map<Integer, List<String>>>();

   }

   public void createOrSelectWorksheet(UIWorksheet uiWorksheet)
   {
      createOrSelectWorksheet(uiWorksheet.getName(), uiWorksheet.getStartRow(), uiWorksheet.getStartColumn());

   }

   public void createOrSelectWorksheet(String worksheetName, Integer startRow, Integer startColumn)
   {
      column = 0;
      row = 0;
      if (sheets.contains(sheets))
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
            buffer.append(sheets.get(i)).append("\n");
            for (int j = 0; j < maxrow; j++)
            {
               for (List<String> col : sheet.values())
               {
                  if (col.get(j) != null)
                     buffer.append("\"").append(String.valueOf(col.get(j))).append("\"").append(",");
               }

               buffer.append("\n");
            }

         }
      }
      return buffer.toString().getBytes();
   }

   public void addCell(int sheet, int column, int row, UICell uiCell) throws ExcelWorkbookException
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
      // JPG2ASCII!!1!
   }

   public void addItem(WorksheetItem item)
   {
      UICell cell = (UICell) item;
      addCell(sheet, column, row++, cell);
   }

   public void addTemplate(Template template)
   {
      // TODO Auto-generated method stub
   }

   public void applyWorksheetSettings(UIWorksheet uiWorksheet)
   {
      // TODO Auto-generated method stub
   }

   public void applyColumnSettings(UIColumn uiColumn)
   {
      // TODO Auto-generated method stub
   }

   public void executeCommand(Command command)
   {
      // TODO Auto-generated method stub
   }

}

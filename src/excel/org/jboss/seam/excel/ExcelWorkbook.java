package org.jboss.seam.excel;

import org.jboss.seam.document.DocumentData.DocumentType;
import org.jboss.seam.excel.ui.UIColumn;
import org.jboss.seam.excel.ui.UIWorkbook;
import org.jboss.seam.excel.ui.UIWorksheet;

/**
 * General interface interacting with an Excel Workbook abstraction 
 * 
 * @author Nicklas Karlsson (nickarls@gmail.com)
 * @author Daniel Roth (danielc.roth@gmail.com)
 * @version 0.2
 */
public interface ExcelWorkbook
{

   public abstract DocumentType getDocumentType();

   /**
    * Moves the internal column pointer to the next column, called by the tag to
    * indicate that a new column has been started. If the pointer exceeds the
    * maximum allowed, throws an exception
    * 
    * @since 0.1
    */
   public abstract void nextColumn();

   /**
    * Creates a new worksheet in the workbook (or selects one if it exists).
    * Will require a rework for auto-renaming when support for auto-adding of
    * new worksheets if there are more than 65k rows.
    * 
    * @param uiWorksheet Worksheet to create or select
    * @since 0.1
    */
   public abstract void createOrSelectWorksheet(UIWorksheet uiWorksheet);

   /**
    * Returns the binary data from the internal representation of the workbook
    * 
    * @return the bytes
    * @since 0.1
    */
   public abstract byte[] getBytes();

   /**
    * Intitializes a new workbook. Must be called first
    * 
    * @param uiWorkbook the workbook UI item to create
    * @since 0.1
    */
   public abstract void createWorkbook(UIWorkbook uiWorkbook);

   /**
    * Applies column settings to the current column
    * 
    * @param uiColumn The UI column to inspect for settings
    */
   public abstract void applyColumnSettings(UIColumn uiColumn);

   /**
    * Adds an item (cell, image, hyperlink) to add to the worksheet
    * 
    * @param item The item to add
    */
   public abstract void addItem(WorksheetItem item);

   /**
    * Adds a template to the template stack
    * 
    * @param template The template to add
    */
   public abstract void addTemplate(Template template);

   /**
    * Executes a command for a worksheet
    * 
    * @param command The command to execute
    */
   public abstract void executeCommand(Command command);
}
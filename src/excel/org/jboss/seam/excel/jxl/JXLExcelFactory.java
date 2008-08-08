package org.jboss.seam.excel.jxl;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.component.UIComponent;

import jxl.CellFeatures;
import jxl.CellView;
import jxl.HeaderFooter;
import jxl.SheetSettings;
import jxl.WorkbookSettings;
import jxl.biff.DisplayFormat;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.Orientation;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.format.Pattern;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import org.jboss.seam.core.Interpolator;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.Validation;
import org.jboss.seam.excel.ui.ExcelComponent;
import org.jboss.seam.excel.ui.UIBackground;
import org.jboss.seam.excel.ui.UIBorder;
import org.jboss.seam.excel.ui.UICellFormat;
import org.jboss.seam.excel.ui.UIColumn;
import org.jboss.seam.excel.ui.UIFont;
import org.jboss.seam.excel.ui.UIHeaderFooter;
import org.jboss.seam.excel.ui.UIHeaderFooterCommand;
import org.jboss.seam.excel.ui.UIListValidation;
import org.jboss.seam.excel.ui.UIListValidationItem;
import org.jboss.seam.excel.ui.UINumericValidation;
import org.jboss.seam.excel.ui.UIPrintArea;
import org.jboss.seam.excel.ui.UIPrintTitles;
import org.jboss.seam.excel.ui.UIRangeValidation;
import org.jboss.seam.excel.ui.UIWorkbook;
import org.jboss.seam.excel.ui.UIWorksheetSettings;
import org.jboss.seam.excel.ui.UICell.CellType;
import org.jboss.seam.excel.ui.UINumericValidation.ValidationCondition;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * Superclass for the JExcelAPI factories. Contains common helper methods
 * 
 * @author Nicklas Karlsson (nickarls@gmail.com)
 * @author Daniel Roth (danielc.roth@gmail.com)
 * @version 0.2
 * 
 */
public class JXLExcelFactory
{
   private static Log log = Logging.getLog(JXLExcelFactory.class);

   private static final String JXL_VERTICAL_ALIGNMENT_CLASS_NAME = "jxl.format.VerticalAlignment";
   private static final String JXL_ORIENTATION_CLASS_NAME = "jxl.format.Orientation";
   private static final String JXL_ALIGNMENT_CLASS_NAME = "jxl.format.Alignment";
   private static final String JXL_PATTERN_CLASS_NAME = "jxl.format.Pattern";
   private static final String JXL_BORDER_LINE_STYLE_CLASS_NAME = "jxl.format.BorderLineStyle";
   private static final String JXL_BORDER_CLASS_NAME = "jxl.format.Border";
   private static final String JXL_UNDERLINE_STYLE_CLASS_NAME = "jxl.format.UnderlineStyle";
   private static final String JXL_SCRIPT_STYLE_CLASS_NAME = "jxl.format.ScriptStyle";
   private static final String JXL_COLOR_CLASS_NAME = "jxl.format.Colour";
   private static final String JXL_PAGE_ORIENTATION_CLASS_NAME = "jxl.format.PageOrientation";
   private static final String JXL_PAPER_SIZE_CLASS_NAME = "jxl.format.PaperSize";
   private static final String HEADER_FOOTER_COMMAND_CLASS_NAME = "org.jboss.seam.excel.UIHeaderFooterCommand";

   /**
    * Creates a JExcelAPI Workbook settings object from the UI counterpart.
    * Starts with an empty object and adds the setting only if it is non-null
    * 
    * @param uiWorkbook The UI element to interpret
    * @return The created workbook settings
    */
   public static WorkbookSettings createWorkbookSettings(UIWorkbook uiWorkbook)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating workbook settings from #0", uiWorkbook);
      }
      WorkbookSettings workbookSettings = new WorkbookSettings();
      if (uiWorkbook.getArrayGrowSize() != null)
      {
         workbookSettings.setArrayGrowSize(uiWorkbook.getArrayGrowSize());
      }
      if (uiWorkbook.getAutoFilterDisabled() != null)
      {
         workbookSettings.setAutoFilterDisabled(uiWorkbook.getAutoFilterDisabled());
      }
      if (uiWorkbook.getAutoFilterDisabled() != null)
      {
         workbookSettings.setCellValidationDisabled(uiWorkbook.getAutoFilterDisabled());
      }
      if (uiWorkbook.getCharacterSet() != null)
      {
         workbookSettings.setCharacterSet(uiWorkbook.getCharacterSet());
      }
      if (uiWorkbook.getDrawingsDisabled() != null)
      {
         workbookSettings.setDrawingsDisabled(uiWorkbook.getDrawingsDisabled());
      }
      if (uiWorkbook.getEncoding() != null)
      {
         workbookSettings.setEncoding(uiWorkbook.getEncoding());
      }
      if (uiWorkbook.getExcelDisplayLanguage() != null)
      {
         workbookSettings.setExcelDisplayLanguage(uiWorkbook.getExcelDisplayLanguage());
      }
      if (uiWorkbook.getExcelRegionalSettings() != null)
      {
         workbookSettings.setExcelRegionalSettings(uiWorkbook.getExcelRegionalSettings());
      }
      if (uiWorkbook.getFormulaAdjust() != null)
      {
         workbookSettings.setFormulaAdjust(uiWorkbook.getFormulaAdjust());
      }
      if (uiWorkbook.getGcDisabled() != null)
      {
         workbookSettings.setGCDisabled(uiWorkbook.getGcDisabled());
      }
      if (uiWorkbook.getIgnoreBlanks() != null)
      {
         workbookSettings.setIgnoreBlanks(uiWorkbook.getIgnoreBlanks());
      }
      if (uiWorkbook.getLocale() != null)
      {
         workbookSettings.setLocale(new Locale(uiWorkbook.getLocale()));
      }
      if (uiWorkbook.getMergedCellCheckingDisabled() != null)
      {
         workbookSettings.setMergedCellChecking(uiWorkbook.getMergedCellCheckingDisabled());
      }
      if (uiWorkbook.getNamesDisabled() != null)
      {
         workbookSettings.setNamesDisabled(uiWorkbook.getNamesDisabled());
      }
      if (uiWorkbook.getPropertySets() != null)
      {
         workbookSettings.setPropertySets(uiWorkbook.getPropertySets());
      }
      if (uiWorkbook.getRationalization() != null)
      {
         workbookSettings.setRationalization(uiWorkbook.getRationalization());
      }
      if (uiWorkbook.getSupressWarnings() != null)
      {
         workbookSettings.setSuppressWarnings(uiWorkbook.getSupressWarnings());
      }
      if (uiWorkbook.getTemporaryFileDuringWriteDirectory() != null)
      {
         workbookSettings.setTemporaryFileDuringWriteDirectory(new File(uiWorkbook.getTemporaryFileDuringWriteDirectory()));
      }
      if (uiWorkbook.getUseTemporaryFileDuringWrite() != null)
      {
         workbookSettings.setUseTemporaryFileDuringWrite(uiWorkbook.getUseTemporaryFileDuringWrite());
      }
      return workbookSettings;
   }

   /**
    * Creates a JExcelAPI representation of a page orientation
    * 
    * @param orientation The type of orientation to create
    * @return The page orientation representation
    */
   private static PageOrientation createPageOrientation(String orientation)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating page orientation for #0", orientation);
      }
      try
      {
         return orientation == null ? PageOrientation.LANDSCAPE : (PageOrientation) getConstant(JXL_PAGE_ORIENTATION_CLASS_NAME, orientation.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Page orientation {0} not supported, try {1}", orientation, getValidConstants(JXL_PAGE_ORIENTATION_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of a paper size
    * 
    * @param paperSize The type of paper size to create
    * @return The paper size representation
    */
   private static PaperSize createPaperSize(String paperSize)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating paper size for #0", paperSize);
      }
      try
      {
         return paperSize == null ? PaperSize.A4 : (PaperSize) getConstant(JXL_PAPER_SIZE_CLASS_NAME, paperSize.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Page size {0} not supported, try {1}", paperSize, getValidConstants(JXL_PAPER_SIZE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI header or footer representation. Processes the left,
    * center and right facets using a helper method
    * 
    * @param uiHeaderFooter The UI header or footer to interpret
    * @param headerFooter The JExcelAPI header or footer representation to add
    *           to
    * @return The JExcelAPI header or footer representation
    */
   private static HeaderFooter createHeaderFooter(UIHeaderFooter uiHeaderFooter, HeaderFooter headerFooter)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Processing header/footer #0", uiHeaderFooter);
      }
      processHeaderFooterFacet(headerFooter.getLeft(), uiHeaderFooter.getFacet(UIHeaderFooter.LEFT_FACET));
      processHeaderFooterFacet(headerFooter.getCentre(), uiHeaderFooter.getFacet(UIHeaderFooter.CENTER_FACET));
      processHeaderFooterFacet(headerFooter.getRight(), uiHeaderFooter.getFacet(UIHeaderFooter.RIGHT_FACET));
      return headerFooter;
   }

   /**
    * Processes a header or footer facet. A header or footer facet in JExcelAPI
    * is split into three parts, left, center and right and the UI
    * representation has facets with the saming namings. Gets the requested
    * facet from the UI component and calls helper methods for processing the
    * header commands in sequence
    * 
    * @param headerFooter The JExcelAPI header or footer facet to process
    * @param facetName The name of the facet to process (left, center, right)
    * @param uiHeaderFooter The UI representation to interpret
    */
   private static void processHeaderFooterFacet(HeaderFooter.Contents contents, UIComponent facet)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Processing facet #0 of header/footer #1", facet, contents);
      }
      // No facet found
      if (facet == null)
      {
         return;
      }
      for (UIComponent child : facet.getChildren())
      {
         if (child.getClass() == UIHeaderFooterCommand.class)
         {
            processHeaderFooterCommand(contents, (UIHeaderFooterCommand) child);
         }
      }
   }

   /**
    * Processes a header command and applies it to the JExcelAPI header contents
    * 
    * @param contents The contents to apply the command to (left, center, right)
    * @param command The command to interpret
    */
   private static void processHeaderFooterCommand(HeaderFooter.Contents contents, UIHeaderFooterCommand command)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Processing header/footer command #0", command);
      }
      switch (command.getCommand())
      {
      case append:
         contents.append((String) command.getParameter());
         break;
      case date:
         contents.appendDate();
         break;
      case page_number:
         contents.appendPageNumber();
         break;
      case time:
         contents.appendTime();
         break;
      case total_pages:
         contents.appendTotalPages();
         break;
      case workbook_name:
         contents.appendWorkbookName();
         break;
      case worksheet_name:
         contents.appendWorkSheetName();
         break;
      case font_name:
         contents.setFontName((String) command.getParameter());
         break;
      case font_size:
         contents.setFontSize((Integer) command.getParameter());
         break;
      case toggle_bold:
         contents.toggleBold();
         break;
      case toggle_italics:
         contents.toggleItalics();
         break;
      case toggle_double_underline:
         contents.toggleDoubleUnderline();
         break;
      case toggle_outline:
         contents.toggleOutline();
         break;
      case toggle_shadow:
         contents.toggleShadow();
         break;
      case toggle_strikethrough:
         contents.toggleStrikethrough();
         break;
      case toggle_subscript:
         contents.toggleSubScript();
         break;
      case toggle_superscript:
         contents.toggleSuperScript();
         break;
      default:
         String message = Interpolator.instance().interpolate("Header/Footer command {0} not supported, try {1}", command.getCommand(), getValidConstants(HEADER_FOOTER_COMMAND_CLASS_NAME));
         throw new ExcelWorkbookException(message);
      }
   }

   /**
    * Applies column settings to a column
    * 
    * @param uiColumn The settings to apply
    * @param worksheet The worksheet to apply the column to
    * @param columnIndex The column index to the column
    */
   public static void applyColumnSettings(UIColumn uiColumn, WritableSheet worksheet, int columnIndex)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Applying column settings #0 on column #1", uiColumn, columnIndex);
      }
      CellView cellView = worksheet.getColumnView(columnIndex);
      if (uiColumn.getAutoSize() != null)
      {
         cellView.setAutosize(uiColumn.getAutoSize());
      }
      if (uiColumn.getHidden() != null)
      {
         cellView.setHidden(uiColumn.getHidden());
      }
      if (uiColumn.getWidth() != null)
      {
         cellView.setSize(uiColumn.getWidth());
      }
      worksheet.setColumnView(columnIndex, cellView);
   }

   /**
    * Applies worksheet settings from the template to the settings
    * 
    * @param oldSettings The settings to append to
    * @param template The template to examine for new settings
    */
   public static void applyWorksheetSettings(SheetSettings oldSettings, UIWorksheetSettings template)
   {
      if (template.getAutomaticFormulaCalculation() != null)
      {
         oldSettings.setAutomaticFormulaCalculation(template.getAutomaticFormulaCalculation());
      }
      if (template.getBottomMargin() != null)
      {
         oldSettings.setBottomMargin(template.getBottomMargin());
      }
      if (template.getCopies() != null)
      {
         oldSettings.setCopies(template.getCopies());
      }
      if (template.getDefaultColumnWidth() != null)
      {
         oldSettings.setDefaultColumnWidth(template.getDefaultColumnWidth());
      }
      if (template.getDefaultRowHeight() != null)
      {
         oldSettings.setDefaultRowHeight(template.getDefaultRowHeight());
      }
      if (template.getDisplayZeroValues() != null)
      {
         oldSettings.setDisplayZeroValues(template.getDisplayZeroValues());
      }
      if (template.getFitHeight() != null)
      {
         oldSettings.setFitHeight(template.getFitHeight());
      }
      if (template.getFitToPages() != null)
      {
         oldSettings.setFitToPages(template.getFitToPages());
      }
      if (template.getFitWidth() != null)
      {
         oldSettings.setFitWidth(template.getFitWidth());
      }
      if (template.getFooterMargin() != null)
      {
         oldSettings.setFooterMargin(template.getFooterMargin());
      }
      if (template.getHeaderMargin() != null)
      {
         oldSettings.setHeaderMargin(template.getHeaderMargin());
      }
      if (template.getHidden() != null)
      {
         oldSettings.setHidden(template.getHidden());
      }
      if (template.getHorizontalCentre() != null)
      {
         oldSettings.setHorizontalCentre(template.getHorizontalCentre());
      }
      if (template.getHorizontalFreeze() != null)
      {
         oldSettings.setHorizontalFreeze(template.getHorizontalFreeze());
      }
      if (template.getHorizontalPrintResolution() != null)
      {
         oldSettings.setHorizontalPrintResolution(template.getHorizontalPrintResolution());
      }
      if (template.getLeftMargin() != null)
      {
         oldSettings.setLeftMargin(template.getLeftMargin());
      }
      if (template.getNormalMagnification() != null)
      {
         oldSettings.setNormalMagnification(template.getNormalMagnification());
      }
      if (template.getOrientation() != null)
      {
         oldSettings.setOrientation(createPageOrientation(template.getOrientation()));
      }
      if (template.getPageBreakPreviewMagnification() != null)
      {
         oldSettings.setPageBreakPreviewMagnification(template.getPageBreakPreviewMagnification());
      }
      if (template.getPageBreakPreviewMode() != null)
      {
         oldSettings.setPageBreakPreviewMode(template.getPageBreakPreviewMode());
      }
      if (template.getPageStart() != null)
      {
         oldSettings.setPageStart(template.getPageStart());
      }
      if (template.getPaperSize() != null)
      {
         oldSettings.setPaperSize(createPaperSize(template.getPaperSize()));
      }
      if (template.getPassword() != null)
      {
         oldSettings.setPassword(template.getPassword());
      }
      if (template.getPasswordHash() != null)
      {
         oldSettings.setPasswordHash(template.getPasswordHash());
      }
      if (template.getPrintGridLines() != null)
      {
         oldSettings.setPrintGridLines(template.getPrintGridLines());
      }
      if (template.getPrintHeaders() != null)
      {
         oldSettings.setPrintHeaders(template.getPrintHeaders());
      }
      if (template.getSheetProtected() != null)
      {
         oldSettings.setProtected(template.getSheetProtected());
      }
      if (template.getRecalculateFormulasBeforeSave() != null)
      {
         oldSettings.setRecalculateFormulasBeforeSave(template.getRecalculateFormulasBeforeSave());
      }
      if (template.getRightMargin() != null)
      {
         oldSettings.setRightMargin(template.getRightMargin());
      }
      if (template.getScaleFactor() != null)
      {
         oldSettings.setScaleFactor(template.getScaleFactor());
      }
      if (template.getSelected() != null)
      {
         oldSettings.setSelected(template.getSelected());
      }
      if (template.getShowGridLines() != null)
      {
         oldSettings.setShowGridLines(template.getShowGridLines());
      }
      if (template.getTopMargin() != null)
      {
         oldSettings.setTopMargin(template.getTopMargin());
      }
      if (template.getVerticalCentre() != null)
      {
         oldSettings.setVerticalCentre(template.getVerticalCentre());
      }
      if (template.getVerticalFreeze() != null)
      {
         oldSettings.setVerticalFreeze(template.getVerticalFreeze());
      }
      if (template.getVerticalPrintResolution() != null)
      {
         oldSettings.setVerticalPrintResolution(template.getVerticalPrintResolution());
      }
      if (template.getZoomFactor() != null)
      {
         oldSettings.setZoomFactor(template.getZoomFactor());
      }
      // Iterates through the worksheet settings child elements (print areas,
      // print titles and headers/footers)
      for (UIComponent child : template.getChildren())
      {
         if (child.getClass() == UIPrintArea.class)
         {
            UIPrintArea printArea = (UIPrintArea) child;
            oldSettings.setPrintArea(printArea.getFirstColumn(), printArea.getFirstRow(), printArea.getLastColumn(), printArea.getLastRow());
         }
         else if (child.getClass() == UIPrintTitles.class)
         {
            UIPrintTitles printTitles = (UIPrintTitles) child;
            oldSettings.setPrintTitles(printTitles.getFirstCol(), printTitles.getFirstRow(), printTitles.getLastCol(), printTitles.getLastRow());
         }
         else if (child.getClass() == UIHeaderFooter.class)
         {
            UIHeaderFooter headerFooter = (UIHeaderFooter) child;
            switch (headerFooter.getType())
            {
            case header:
               oldSettings.setHeader(createHeaderFooter(headerFooter, oldSettings.getHeader()));
               break;
            case footer:
               oldSettings.setFooter(createHeaderFooter(headerFooter, oldSettings.getFooter()));
               break;
            default:
               throw new ExcelWorkbookException("Header/Footer type " + headerFooter.getType() + " not supported, try [header, footer]");
            }
         }
      }
   }

   /**
    * Creates a JExcelAPI cell representation from the given input
    * 
    * @param column The row (0-based) to place the cell at
    * @param row The column (0-based) to place the cell at
    * @param type The type of cell
    * @param data The contents of the cell
    * @param cellFormat The cell format settings of the cell
    * @return The prepared cell representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/write/WritableCell.html">WritableCell</a>
    * @since 0.1
    */
   public static WritableCell createCell(int column, int row, CellType type, Object data, WritableCellFormat cellFormat)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating cell at (#0,#1) of type #2 with data #2", column, row, type, data);
      }

      switch (type)
      {
      case text:
         return new Label(column, row, data.toString(), cellFormat);
      case number:
         return new jxl.write.Number(column, row, Double.parseDouble(data.toString()), cellFormat);
      case date:
         return new DateTime(column, row, (Date) data, cellFormat);
      case formula:
         return new Formula(column, row, data.toString(), cellFormat);
      case bool:
         return new jxl.write.Boolean(column, row, Boolean.parseBoolean(data.toString()), cellFormat);
      default:
         return new Label(column, row, data.toString(), cellFormat);
      }
   }

   /**
    * Creates a JExcelAPI font representation from the UI counterpart. Starting
    * with a fresh cell or template and only applies settings that are non-null
    * 
    * @param uiFont The font settings to interpret
    * @param templateFont The font to use as a template
    * @return The font representation
    * @throws WriteException If there is an error creating the font
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/write/WritableFont.html">WritableFont</a>
    * @since 0.1
    */
   private static WritableFont createFont(UIFont uiFont, Font templateFont) throws WriteException
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating font for #0", uiFont);
      }
      WritableFont font = null;

      if (uiFont.getName() != null)
      {
         font = new WritableFont(WritableFont.createFont(uiFont.getName()));
      }
      else if (templateFont != null)
      {
         font = new WritableFont(templateFont);
      }
      else
      {
         font = new WritableFont(WritableFont.ARIAL);
      }

      if (uiFont.getColor() != null)
      {
         font.setColour(createColor(uiFont.getColor()));
      }
      if (uiFont.getPointSize() != null)
      {
         font.setPointSize(uiFont.getPointSize());
      }
      if (uiFont.getBold() != null)
      {
         font.setBoldStyle(uiFont.getBold() ? WritableFont.BOLD : WritableFont.NO_BOLD);
      }
      if (uiFont.getItalic() != null)
      {
         font.setItalic(uiFont.getItalic());
      }
      if (uiFont.getStruckOut() != null)
      {
         font.setStruckout(uiFont.getStruckOut());
      }
      if (uiFont.getScriptStyle() != null)
      {
         font.setScriptStyle(createScriptStyle(uiFont.getScriptStyle()));
      }
      if (uiFont.getUnderlineStyle() != null)
      {
         font.setUnderlineStyle(createUnderlineStyle(uiFont.getUnderlineStyle()));
      }
      return font;
   }

   /**
    * Creates a JExcelAPI representation of a number mask
    * 
    * @param mask The requested mask
    * @return The mask representation or null if the mask couldn't be created
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/write/NumberFormats.html">NumberFormats</a>
    * @since 0.1
    */
   private static DisplayFormat createNumberFormat(String mask)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating number format for mask #0", mask);
      }
      try
      {
         return (DisplayFormat) getConstant("jxl.write.NumberFormats", mask);
      }
      catch (Exception e)
      {
         return null;
      }
   }

   /**
    * Creates a JExcelAPI representation of a date mask
    * 
    * @param mask The requested mask
    * @return The mask representation or null if the mask couldn't be created
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/write/DateFormats.html">DateFormats</a>
    * @since 0.1
    */
   private static DisplayFormat createDateFormat(String mask)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating date format for mask #0", mask);
      }
      try
      {
         return (DisplayFormat) getConstant("jxl.write.DateFormats", mask.toUpperCase());
      }
      catch (Exception e)
      {
         return null;
      }
   }

   /**
    * Creates a JExcelAPI representation of an vertical alignment
    * 
    * @param mask The requested alignment
    * @return The alignment representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/VerticalAlignment.html">VerticalAlignment</a>
    * @since 0.1
    */
   private static VerticalAlignment createVerticalAlignment(String verticalAlignment)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating verical alignment for #0", verticalAlignment);
      }
      try
      {
         return verticalAlignment == null ? VerticalAlignment.BOTTOM : (VerticalAlignment) getConstant(JXL_VERTICAL_ALIGNMENT_CLASS_NAME, verticalAlignment.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Verical alignment {0} not supported, try {1}", verticalAlignment, getValidConstants(JXL_VERTICAL_ALIGNMENT_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of an orientation
    * 
    * @param mask The requested orientation
    * @return The orientation representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/Orientation.html">Orientation</a>
    * @since 0.1
    */
   private static Orientation createOrientation(String orientation)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating orientation for #0", orientation);
      }
      try
      {
         return orientation == null ? Orientation.HORIZONTAL : (Orientation) getConstant(JXL_ORIENTATION_CLASS_NAME, orientation.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Orientation {0} not supported, try {1}", orientation, getValidConstants(JXL_ORIENTATION_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of an alignment
    * 
    * @param mask The requested alignment
    * @return The alignment representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/Alignment.html">Alignment</a>
    * @since 0.1
    */
   private static Alignment createAlignment(String alignment)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating alignment for #0", alignment);
      }
      try
      {
         return alignment == null ? Alignment.LEFT : (Alignment) getConstant(JXL_ALIGNMENT_CLASS_NAME, alignment.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Alignment {0} not supported, try {1}", alignment, getValidConstants(JXL_ALIGNMENT_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of an pattern
    * 
    * @param mask The requested pattern
    * @return The pattern representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/Pattern.html">Pattern</a>
    * @since 0.1
    */
   private static Pattern createPattern(String pattern)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating pattern for #0", pattern);
      }
      try
      {
         return pattern == null ? Pattern.SOLID : (Pattern) getConstant(JXL_PATTERN_CLASS_NAME, pattern.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Pattern {0} not supported, try {1}", pattern, getValidConstants(JXL_PATTERN_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of a border line style
    * 
    * @param mask The requested border line style
    * @return The border line style representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/BorderlineStyle.html">BorderlineStyle</a>
    * @since 0.1
    */
   private static BorderLineStyle createBorderLineStyle(String borderLineStyle)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating border line style for #0", borderLineStyle);
      }
      try
      {
         return borderLineStyle == null ? BorderLineStyle.NONE : (BorderLineStyle) getConstant(JXL_BORDER_LINE_STYLE_CLASS_NAME, borderLineStyle.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Border line style {0} not supported, try {1}", borderLineStyle, getValidConstants(JXL_BORDER_LINE_STYLE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of a border
    * 
    * @param mask The requested border
    * @return The border representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/Border.html"></a>
    * @since 0.1
    */
   private static Border createBorder(String border)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating border for #0", border);
      }
      try
      {
         return border == null ? Border.ALL : (Border) getConstant(JXL_BORDER_CLASS_NAME, border.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Border {0} not supported, try {1}", border, getValidConstants(JXL_BORDER_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of an underline style
    * 
    * @param mask The requested underline style
    * @return The underline style representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/UnderlineStyle.html">UnderlineStyle</a>
    * @since 0.1
    */
   private static UnderlineStyle createUnderlineStyle(String underlineStyle)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating underline style for #0", underlineStyle);
      }
      try
      {
         return underlineStyle == null ? UnderlineStyle.NO_UNDERLINE : (UnderlineStyle) getConstant(JXL_UNDERLINE_STYLE_CLASS_NAME, underlineStyle.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Underline style {0} not supported, try {1}", underlineStyle, getValidConstants(JXL_UNDERLINE_STYLE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of an script style
    * 
    * @param mask The requested script style
    * @return The script style representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/ScriptStyle.html">ScriptStyle</a>
    * @since 0.1
    */
   private static ScriptStyle createScriptStyle(String scriptStyle)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating script style for #0", scriptStyle);
      }
      try
      {
         return scriptStyle == null ? ScriptStyle.NORMAL_SCRIPT : (ScriptStyle) getConstant(JXL_SCRIPT_STYLE_CLASS_NAME, scriptStyle.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Script style {0} not supported, try {1}", scriptStyle, getValidConstants(JXL_SCRIPT_STYLE_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Creates a JExcelAPI representation of a color
    * 
    * @param mask The requested color
    * @return The color representation
    * @see <a
    *      href="http://jexcelapi.sourceforge.net/resources/javadocs/2_6/docs/jxl/format/Colour.html">Colour</a>
    * @since 0.1
    */
   private static Colour createColor(String color)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating color for #0", color);
      }
      // Workaround for the feature that black is... well not always black in
      // Excel (ref: Andy Khan on yahoo groups)
      if (color.equalsIgnoreCase("black"))
      {
         color = "palette_black";
      }
      try
      {
         return color == null ? Colour.AUTOMATIC : (Colour) getConstant(JXL_COLOR_CLASS_NAME, color.toUpperCase());
      }
      catch (Exception e)
      {
         String message = Interpolator.instance().interpolate("Color {0} not supported, try {1}", color, getValidConstants(JXL_COLOR_CLASS_NAME));
         throw new ExcelWorkbookException(message, e);
      }
   }

   /**
    * Adds list validation to a cell
    * 
    * @param cellFeatures The cell features to add validation to
    * @param validation The validation to parse
    */
   private static void addListValidation(WritableCellFeatures cellFeatures, UIListValidation validation)
   {
      List<UIListValidationItem> items = ExcelComponent.getChildrenOfType(validation.getChildren(), UIListValidationItem.class);
      if (items.isEmpty())
      {
         throw new ExcelWorkbookException("No items in validation list");
      }

      List<String> validations = new ArrayList<String>();
      for (UIListValidationItem item : items)
      {
         validations.add(item.getValue());
      }

      cellFeatures.setDataValidationList(validations);
   }

   /**
    * Adds range validation to a cell
    * 
    * @param cellFeatures The cell features to apply the validation to
    * @param validation The validation to add
    */
   private static void addRangeValidation(WritableCellFeatures cellFeatures, UIRangeValidation validation)
   {
      if (validation.getStartColumn() == null || validation.getStartRow() == null || validation.getEndColumn() == null || validation.getEndRow() == null)
      {
         throw new ExcelWorkbookException("Must set all start/end columns/rows for range validation");
      }

      cellFeatures.setDataValidationRange(validation.getStartColumn(), validation.getStartRow(), validation.getEndColumn(), validation.getEndRow());
   }

   private static void addNumericValidation(WritableCellFeatures cellFeatures, UINumericValidation validation)
   {
      if (validation.getValue() == null)
      {
         throw new ExcelWorkbookException("Must define value in validation");
      }
      if ((ValidationCondition.between.equals(validation.getCondition()) || ValidationCondition.not_between.equals(validation.getCondition())) && validation.getValue2() == null)
      {
         throw new ExcelWorkbookException("Must define both values in validation for between/not_between");
      }
      switch (validation.getCondition())
      {
      case equal:
         cellFeatures.setNumberValidation(validation.getValue(), WritableCellFeatures.EQUAL);
         break;
      case not_equal:
         cellFeatures.setNumberValidation(validation.getValue(), WritableCellFeatures.NOT_EQUAL);
         break;
      case greater_equal:
         cellFeatures.setNumberValidation(validation.getValue(), WritableCellFeatures.GREATER_EQUAL);
         break;
      case less_equal:
         cellFeatures.setNumberValidation(validation.getValue(), WritableCellFeatures.LESS_EQUAL);
         break;
      case less_than:
         cellFeatures.setNumberValidation(validation.getValue(), WritableCellFeatures.LESS_THAN);
         break;
      case between:
         cellFeatures.setNumberValidation(validation.getValue(), validation.getValue2(), WritableCellFeatures.BETWEEN);
         break;
      case not_between:
         cellFeatures.setNumberValidation(validation.getValue(), validation.getValue2(), WritableCellFeatures.NOT_BETWEEN);
         break;
      }
   }

   /**
    * Creates a cell format from a blank cell or from a template, merges with a
    * previous format
    * 
    * @param mergeCellFormat The cell format to merge
    * @param templateCellFormat The cell format to use as a template
    * @param dataType The data type of the cell requesting the format
    * @return The merged cell format
    * @throws WriteException If the cell format couldn't be created
    */
   public static WritableCellFormat createCellFormat(UICellFormat mergeCellFormat, WritableCellFormat templateCellFormat, CellType dataType) throws WriteException
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating cell format for #0 with type #1 and template #2", mergeCellFormat, dataType, templateCellFormat);
      }
      WritableCellFormat cellFormat = null;

      switch (dataType)
      {
      case text:
         // Creates a basic text format
         cellFormat = templateCellFormat == null ? new WritableCellFormat(NumberFormats.TEXT) : new WritableCellFormat(templateCellFormat);
         break;
      case number:
         /*
          * If there is no mask, creates a default number format cell If there
          * is a mask, tries to match it against a constant name If the constant
          * can't be created, creates a custom number format from the mask
          */
         if (mergeCellFormat.getMask() == null)
         {
            cellFormat = templateCellFormat == null ? new WritableCellFormat(NumberFormats.DEFAULT) : new WritableCellFormat(templateCellFormat);
         }
         else
         {
            DisplayFormat displayFormat = createNumberFormat(mergeCellFormat.getMask());
            if (displayFormat != null)
            {
               cellFormat = mergeCellFormat == null ? new WritableCellFormat(displayFormat) : new WritableCellFormat(templateCellFormat);
            }
            else
            {
               try
               {
                  cellFormat = templateCellFormat == null ? new WritableCellFormat(new NumberFormat(mergeCellFormat.getMask())) : new WritableCellFormat(templateCellFormat);
               }
               catch (IllegalArgumentException e)
               {
                  throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not create number format for mask {0}", mergeCellFormat.getMask()), e);
               }
            }
         }
         break;
      case date:
         /*
          * If there is no mask, creates a default date format cell If there is
          * a mask, tries to match it against a constant name If the constant
          * can't be created, creates a custom date format from the mask
          */

         if (mergeCellFormat.getMask() == null)
         {
            cellFormat = templateCellFormat == null ? new WritableCellFormat(DateFormats.DEFAULT) : new WritableCellFormat(templateCellFormat);
         }
         else
         {
            DisplayFormat displayFormat = createDateFormat(mergeCellFormat.getMask());
            if (displayFormat != null)
            {
               cellFormat = templateCellFormat == null ? new WritableCellFormat(displayFormat) : new WritableCellFormat(templateCellFormat);
            }
            else
            {
               try
               {
                  cellFormat = templateCellFormat == null ? new WritableCellFormat(new DateFormat(mergeCellFormat.getMask())) : new WritableCellFormat(templateCellFormat);
               }
               catch (IllegalArgumentException e)
               {
                  throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not create date format for mask {0}", mergeCellFormat.getMask()), e);
               }
            }
         }
         break;
      case formula:
         cellFormat = templateCellFormat == null ? new WritableCellFormat() : new WritableCellFormat(templateCellFormat);
         break;
      case bool:
         cellFormat = templateCellFormat == null ? new WritableCellFormat() : new WritableCellFormat(templateCellFormat);
         break;
      default:
         cellFormat = templateCellFormat == null ? new WritableCellFormat() : new WritableCellFormat(templateCellFormat);
         break;
      }

      if (mergeCellFormat.getAlignment() != null)
      {
         cellFormat.setAlignment(createAlignment(mergeCellFormat.getAlignment()));
      }
      if (mergeCellFormat.getIndentation() != null)
      {
         cellFormat.setIndentation(mergeCellFormat.getIndentation());
      }
      if (mergeCellFormat.getLocked() != null)
      {
         cellFormat.setLocked(mergeCellFormat.getLocked());
      }
      if (mergeCellFormat.getOrientation() != null)
      {
         cellFormat.setOrientation(createOrientation(mergeCellFormat.getOrientation()));
      }
      if (mergeCellFormat.getShrinkToFit() != null)
      {
         cellFormat.setShrinkToFit(mergeCellFormat.getShrinkToFit());
      }
      if (mergeCellFormat.getVerticalAlignment() != null)
      {
         cellFormat.setVerticalAlignment(createVerticalAlignment(mergeCellFormat.getVerticalAlignment()));
      }
      if (mergeCellFormat.getWrap() != null)
      {
         cellFormat.setWrap(mergeCellFormat.getWrap());
      }
      for (UIComponent child : mergeCellFormat.getChildren())
      {
         if (child instanceof UIFont)
         {
            Font templateFont = templateCellFormat == null ? null : templateCellFormat.getFont();
            cellFormat.setFont(createFont((UIFont) child, templateFont));
         }
         else if (child.getClass() == UIBorder.class)
         {
            cellFormat.setBorder(createBorder(((UIBorder) child).getBorder()), createBorderLineStyle(((UIBorder) child).getLineStyle()), createColor(((UIBorder) child).getColor()));
         }
         else if (child.getClass() == UIBackground.class)
         {
            cellFormat.setBackground(createColor(((UIBackground) child).getColor()), createPattern(((UIBackground) child).getPattern()));
         }
         else
         {
            // throw new ExcelWorkbookException("Invalid UICell child class " +
            // child.getClass().getName());
         }
      }

      return cellFormat;
   }

   /**
    * Creates cell features from a template
    * 
    * @param uiCellFormat The cell format to apply
    * @param template The template to use as a base
    * @return The cell features
    */
   public static WritableCellFeatures createCellFeatures(UICellFormat uiCellFormat, CellFeatures template)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Creating cell features for #0", uiCellFormat);
      }
      WritableCellFeatures cellFeatures = template != null ? new WritableCellFeatures(template) : new WritableCellFeatures();

      if (uiCellFormat.getComment() != null)
      {
         if (uiCellFormat.getCommentHeight() != null && uiCellFormat.getCommentWidth() != null)
         {
            cellFeatures.setComment(uiCellFormat.getComment(), uiCellFormat.getCommentWidth(), uiCellFormat.getCommentHeight());
         }
         else
         {
            cellFeatures.setComment(uiCellFormat.getComment());
         }
      }
      List<Validation> validations = ExcelComponent.getChildrenOfType(uiCellFormat.getChildren(), Validation.class);
      for (Validation validation : validations)
      {
         switch (validation.getType())
         {
         case numeric:
            addNumericValidation(cellFeatures, (UINumericValidation) validation);
            break;
         case range:
            addRangeValidation(cellFeatures, (UIRangeValidation) validation);
            break;
         case list:
            addListValidation(cellFeatures, (UIListValidation) validation);
            break;
         default:
            throw new ExcelWorkbookException(Interpolator.instance().interpolate("Unknown validation type {0}", validation.getType()));
         }
      }
      return cellFeatures;
   }

   /**
    * Gets a static constant from a class
    * 
    * @param className The name of the class containing the constant
    * @param fieldName The name of the constant
    * @return The constant
    * @throws Exception If there is a reflection error fetching the data.
    */
   protected static Object getConstant(String className, String fieldName) throws Exception
   {
      if (log.isTraceEnabled())
      {
         log.trace("Looking for constant #0 in class #1", fieldName, className);
      }
      return Class.forName(className).getField(fieldName).get(null);
   }

   /**
    * Fetches a list of public static constants in a class. Used for showing
    * valid values in case of an exception fetching e.g. constants from a class.
    * 
    * @param className The name of the class to inspect
    * @return A comma separated string with declared constants in the class
    */
   @SuppressWarnings("unchecked")
   protected static String getValidConstants(String className)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Getting valid constants from #0", className);
      }
      Class clazz = null;
      try
      {
         clazz = Class.forName(className);
      }
      catch (ClassNotFoundException e)
      {
         throw new ExcelWorkbookException("Could not find class while getting valid constants", e);
      }
      StringBuffer buffer = new StringBuffer();
      int i = 0;
      // Loop through the fields
      for (Field field : clazz.getFields())
      {
         int modifiers = field.getModifiers();
         // Append to list if it's public and static (as most our constants are)
         if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers))
         {
            String name = field.getName().toLowerCase();
            buffer.append(i++ == 0 ? name : ", " + name);
         }
      }
      return Interpolator.instance().interpolate("[#0]", buffer.toString());
   }

}

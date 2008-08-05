package org.jboss.seam.test.excel.unit;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jxl.Workbook;
import jxl.format.BoldStyle;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;

import org.jboss.seam.document.DocumentData;
import org.jboss.seam.excel.ExcelWorkbook;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.jxl.JXLExcelWorkbook;
import org.jboss.seam.excel.ui.UIBorder;
import org.jboss.seam.excel.ui.UICell;
import org.jboss.seam.excel.ui.UICellTemplate;
import org.jboss.seam.excel.ui.UIColumn;
import org.jboss.seam.excel.ui.UIFont;
import org.jboss.seam.excel.ui.UIHeaderFooter;
import org.jboss.seam.excel.ui.UIHeaderFooterCommand;
import org.jboss.seam.excel.ui.UIHeaderFooterCommands;
import org.jboss.seam.excel.ui.UIHyperlink;
import org.jboss.seam.excel.ui.UIImage;
import org.jboss.seam.excel.ui.UIListValidation;
import org.jboss.seam.excel.ui.UIListValidationItem;
import org.jboss.seam.excel.ui.UIMergeCells;
import org.jboss.seam.excel.ui.UINumericValidation;
import org.jboss.seam.excel.ui.UIRangeValidation;
import org.jboss.seam.excel.ui.UIWorkbook;
import org.jboss.seam.excel.ui.UIWorksheet;
import org.jboss.seam.excel.ui.UIWorksheetTemplate;
import org.jboss.seam.excel.ui.UIHeaderFooter.Type;
import org.jboss.seam.excel.ui.UIHeaderFooterCommand.Command;
import org.jboss.seam.excel.ui.UINumericValidation.ValidationCondition;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class JXLExcelWorkbookTest
{
   ExcelWorkbook excelWorkbook;
   Workbook workbook;
   ByteArrayInputStream ins;

   public void testApplyColumnSettings()
   {
      UIColumn column = new UIColumn();
      column.setWidth(1000);
      excelWorkbook.applyColumnSettings(column);
      flushWorkbook();
      assert workbook.getSheet(0).getColumnView(0).getSize() == 1000;

   }

   public void testMergeCellCommand()
   {
      UIMergeCells c = new UIMergeCells();
      c.setStartColumn(1);
      c.setStartRow(1);
      c.setEndColumn(5);
      c.setEndRow(5);
      excelWorkbook.executeCommand(c);
      flushWorkbook();
      assert workbook.getSheet(0).getMergedCells().length == 1;
      assert workbook.getSheet(0).getMergedCells()[0].getTopLeft().getColumn() == 1;
      assert workbook.getSheet(0).getMergedCells()[0].getTopLeft().getRow() == 1;
      assert workbook.getSheet(0).getMergedCells()[0].getBottomRight().getColumn() == 5;
      assert workbook.getSheet(0).getMergedCells()[0].getBottomRight().getRow() == 5;
   }

   public void testNumericValidation()
   {
      UICell c = new UICell();
      c.setValue(5);
      UINumericValidation v = new UINumericValidation();
      v.setCondition(ValidationCondition.greater_equal);
      v.setValue(10d);
      c.getChildren().add(v);
      excelWorkbook.addItem(c);
      flushWorkbook();
      // assert workbook.getSheet(0).getCell(0,
      // 0).getCellFeatures().getDataValidationList().length() == 1;
   }

   public void testRangeValidation()
   {
      UICell c = new UICell();
      c.setValue(5);
      UIRangeValidation v = new UIRangeValidation();
      v.setStartColumn(2);
      v.setStartRow(2);
      v.setEndColumn(4);
      v.setEndRow(4);
      c.getChildren().add(v);
      excelWorkbook.addItem(c);
      flushWorkbook();
      // assert workbook.getSheet(0).getCell(0,
      // 0).getCellFeatures().getDataValidationList().length() == 1;
   }

   public void testListValidation()
   {
      UICell c = new UICell();
      c.setValue("foo");
      UIListValidation v = new UIListValidation();
      UIListValidationItem i = new UIListValidationItem();
      i.setValue("foo");
      UIListValidationItem i2 = new UIListValidationItem();
      i2.setValue("bar");
      v.getChildren().add(i);
      v.getChildren().add(i2);
      c.getChildren().add(v);
      excelWorkbook.addItem(c);
      flushWorkbook();
      // assert workbook.getSheet(0).getCell(0,
      // 0).getCellFeatures().getDataValidationList().length() == 1;
   }

   public void testHeaderCommand()
   {
      UIWorksheet w = new UIWorksheet();
      UIHeaderFooterCommands hfc = new UIHeaderFooterCommands();
      UIHeaderFooterCommand c = new UIHeaderFooterCommand();
      c.setCommand(Command.time);
      hfc.getChildren().add(c);
      UIHeaderFooter hf = new UIHeaderFooter();
      hf.setType(Type.header);
      hf.getFacets().put("left", hfc);
      w.getChildren().add(hf);
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      excelWorkbook.createOrSelectWorksheet(w);
      flushWorkbook();
      assert !workbook.getSheet(0).getSettings().getHeader().getLeft().empty();
   }

   public void testApplyWorksheetSettings() throws IOException
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      UIWorksheet uiWorksheet = new UIWorksheet();
      uiWorksheet.setCopies(5);
      excelWorkbook.createOrSelectWorksheet(uiWorksheet);
      flushWorkbook();
      assert workbook.getSheet(0).getSettings().getCopies() == 5;
   }

   @Test(expectedExceptions = ExcelWorkbookException.class)
   public void testApplyColumnSettingsOnNoWorksheet()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      excelWorkbook.applyColumnSettings(new UIColumn());
      assert false;
   }

   @Test(expectedExceptions = ExcelWorkbookException.class)
   public void testAddWorksheetOnNoWorkbook()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createOrSelectWorksheet(new UIWorksheet());
      assert false;
   }

   public void testCreateWorkSheetWithNullParameters()
   {
      flushWorkbook();
      assert workbook.getSheets().length == 1;
      assert workbook.getSheetNames()[0].equals("Sheet1");
   }

   public void testCreateWorksheetWithName()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      UIWorksheet uiWorksheet = new UIWorksheet();
      uiWorksheet.setName("Test");
      excelWorkbook.createOrSelectWorksheet(uiWorksheet);
      flushWorkbook();
      assert workbook.getSheets().length == 1;
      assert workbook.getSheetNames()[0].equals("Test");
   }

   public void testAddCell()
   {
      UICell uiCell = new UICell();
      uiCell.setValue("ping");
      excelWorkbook.addItem(uiCell);
      flushWorkbook();
      assert workbook.getSheet(0).getCell(0, 0).getContents().equals("ping");
   }

//   public void testAddImage()
//   {
//      UIImage image = new UIImage();
//      image.setURI("file:////seam.gif");
//      excelWorkbook.addItem(image);
//      flushWorkbook();
//      assert workbook.getSheet(0).getDrawing(0) != null;
//   }

   public void testAddHyperlink()
   {
      UIHyperlink link = new UIHyperlink();
      link.setURL("http://www.seamframework.org");
      excelWorkbook.addItem(link);
      flushWorkbook();
      assert workbook.getSheet(0).getHyperlinks().length == 1;
      assert workbook.getSheet(0).getHyperlinks()[0].getURL().getProtocol().equals("http");
      assert workbook.getSheet(0).getHyperlinks()[0].getURL().getHost().equals("www.seamframework.org");
      assert workbook.getSheet(0).getHyperlinks()[0].getColumn() == 0;
      assert workbook.getSheet(0).getHyperlinks()[0].getRow() == 0;
   }

   public void testAddTargettedCell()
   {
      UICell uiCell = new UICell();
      uiCell.setValue("ping");
      uiCell.setColumn(10);
      uiCell.setRow(10);
      excelWorkbook.addItem(uiCell);
      flushWorkbook();
      assert workbook.getSheet(0).getCell(10, 10).getContents().equals("ping");
   }

//   public void testComment()
//   {
//      UICell uiCell = new UICell();
//      uiCell.setComment("comment!");
//      excelWorkbook.addItem(uiCell);
//      flushWorkbook();
//      assert workbook.getSheet(0).getCell(0, 0).getCellFeatures().getComment().equals("comment!");
//   }

   public void testAddMultipleCells()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      UIWorksheet uiWorksheet = new UIWorksheet();
      uiWorksheet.setStartColumn(10);
      uiWorksheet.setStartRow(10);
      excelWorkbook.createOrSelectWorksheet(uiWorksheet);
      UICell uiCell = new UICell();
      uiCell.setValue("ping");
      excelWorkbook.addItem(uiCell);
      uiCell.setValue("pong");
      excelWorkbook.addItem(uiCell);
      flushWorkbook();
      assert workbook.getSheet(0).getCell(10, 10).getContents().equals("ping");
      assert workbook.getSheet(0).getCell(10, 11).getContents().equals("pong");
   }

   public void testNextColumn()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      UIWorksheet uiWorksheet = new UIWorksheet();
      uiWorksheet.setStartColumn(10);
      uiWorksheet.setStartRow(10);
      excelWorkbook.createOrSelectWorksheet(uiWorksheet);
      UICell uiCell = new UICell();
      uiCell.setValue("ping");
      excelWorkbook.addItem(uiCell);
      excelWorkbook.nextColumn();
      UICell uiCell2 = new UICell();
      uiCell2.setValue("pong");
      excelWorkbook.addItem(uiCell2);
      flushWorkbook();
      assert workbook.getSheet(0).getCell(10, 10).getContents().equals("ping");
      assert workbook.getSheet(0).getCell(11, 10).getContents().equals("pong");
   }

   public void testDocumentType()
   {
      DocumentData.DocumentType documentType = excelWorkbook.getDocumentType();
      assert documentType.getExtension().equals("xls");
      assert documentType.getMimeType().equals("application/vnd.ms-excel");
   }

   public void testSelectWorksheet()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      UIWorksheet uiWorksheet = new UIWorksheet();
      uiWorksheet.setStartColumn(10);
      uiWorksheet.setStartRow(10);
      uiWorksheet.setName("first");
      excelWorkbook.createOrSelectWorksheet(uiWorksheet);
      UICell uiCell = new UICell();
      uiCell.setValue("ping");
      excelWorkbook.addItem(uiCell);
      UIWorksheet uiWorksheet2 = new UIWorksheet();
      uiWorksheet2.setStartColumn(10);
      uiWorksheet2.setStartRow(10);
      uiWorksheet2.setName("second");
      excelWorkbook.createOrSelectWorksheet(uiWorksheet2);
      excelWorkbook.createOrSelectWorksheet(uiWorksheet);
      uiCell.setValue("pong");
      excelWorkbook.addItem(uiCell);
      flushWorkbook();
      assert workbook.getSheets().length == 2;
      assert workbook.getSheet(0).getCell(10, 10).getContents().equals("pong");
   }

   public void testCascadingWorksheetSettings()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());

      UIWorksheetTemplate t = new UIWorksheetTemplate();
      t.setName("a");
      t.setCopies(5);
      UIHeaderFooterCommands hfc1 = new UIHeaderFooterCommands();
      UIHeaderFooterCommand c1 = new UIHeaderFooterCommand();
      c1.setCommand(Command.time);
      hfc1.getChildren().add(c1);
      UIHeaderFooter hf1 = new UIHeaderFooter();
      hf1.setType(Type.header);
      hf1.getFacets().put("right", hfc1);
      t.getChildren().add(hf1);
      excelWorkbook.addTemplate(t);

      UIWorksheetTemplate t2 = new UIWorksheetTemplate();
      t2.setName("b");
      t2.setFitWidth(666);
      UIHeaderFooterCommands hfc2 = new UIHeaderFooterCommands();
      UIHeaderFooterCommand c2 = new UIHeaderFooterCommand();
      c2.setCommand(Command.time);
      hfc2.getChildren().add(c2);
      UIHeaderFooter hf2 = new UIHeaderFooter();
      hf2.setType(Type.header);
      hf2.getFacets().put("center", hfc2);
      t2.getChildren().add(hf2);
      excelWorkbook.addTemplate(t2);

      UIWorksheet s = new UIWorksheet();
      s.setFitHeight(333);
      s.setTemplates("a,b");
      UIHeaderFooterCommands hfc3 = new UIHeaderFooterCommands();
      UIHeaderFooterCommand c3 = new UIHeaderFooterCommand();
      c3.setCommand(Command.time);
      hfc3.getChildren().add(c3);
      UIHeaderFooter hf3 = new UIHeaderFooter();
      hf3.setType(Type.header);
      hf3.getFacets().put("left", hfc3);
      s.getChildren().add(hf3);

      excelWorkbook.createOrSelectWorksheet(s);
      flushWorkbook();
      assert workbook.getSheet(0).getSettings().getCopies() == 5;
      assert workbook.getSheet(0).getSettings().getFitWidth() == 666;
      assert workbook.getSheet(0).getSettings().getFitHeight() == 333;
      assert !workbook.getSheet(0).getSettings().getHeader().getLeft().empty();
      assert !workbook.getSheet(0).getSettings().getHeader().getCentre().empty();
      assert !workbook.getSheet(0).getSettings().getHeader().getRight().empty();
   }

   public void testCascadingCellTemplate()
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      excelWorkbook.createOrSelectWorksheet(new UIWorksheet());

      UICellTemplate t = new UICellTemplate();
      UIFont f = new UIFont();
      f.setBold(true);
      t.getChildren().add(f);
      t.setName("a");
      UIBorder b = new UIBorder();
      b.setBorder("all");
      b.setColor("blue");
      b.setLineStyle("thick");
      t.getChildren().add(b);
      excelWorkbook.addTemplate(t);

      UICellTemplate t2 = new UICellTemplate();
      UIFont f2 = new UIFont();
      f2.setColor("blue");
      t2.getChildren().add(f2);
      t2.setName("b");
      UIBorder b2 = new UIBorder();
      b2.setBorder("left");
      b2.setColor("red");
      b2.setLineStyle("thin");
      t.getChildren().add(b2);
      excelWorkbook.addTemplate(t2);

      UICell c = new UICell();
      c.setValue("foo");
      c.setTemplates("a,b");
      excelWorkbook.addItem(c);

      flushWorkbook();

      assert workbook.getSheet(0).getCell(0, 0).getCellFormat().getFont().getBoldWeight() == BoldStyle.BOLD.getValue();
      assert workbook.getSheet(0).getCell(0, 0).getCellFormat().getFont().getColour().equals(Colour.BLUE);
      assert workbook.getSheet(0).getCell(0, 0).getCellFormat().getBorderColour(Border.TOP).equals(Colour.BLUE);
      assert workbook.getSheet(0).getCell(0, 0).getCellFormat().getBorderLine(Border.TOP).equals(BorderLineStyle.THICK);
      assert workbook.getSheet(0).getCell(0, 0).getCellFormat().getBorderColour(Border.LEFT).equals(Colour.RED);
      assert workbook.getSheet(0).getCell(0, 0).getCellFormat().getBorderLine(Border.LEFT).equals(BorderLineStyle.THIN);
   }

   protected void flushWorkbook()
   {
      ins = new ByteArrayInputStream(excelWorkbook.getBytes());
      try
      {
         workbook = Workbook.getWorkbook(ins);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @BeforeMethod
   public void setup() throws IOException
   {
      excelWorkbook = new JXLExcelWorkbook();
      excelWorkbook.createWorkbook(new UIWorkbook());
      excelWorkbook.createOrSelectWorksheet(new UIWorksheet());
   }

   @AfterMethod
   public void cleanup() throws IOException
   {
      if (workbook != null)
      {
         workbook.close();
      }
      workbook = null;
      excelWorkbook = null;
      if (ins != null)
      {
         ins.close();
      }
      ins = null;
   }

}

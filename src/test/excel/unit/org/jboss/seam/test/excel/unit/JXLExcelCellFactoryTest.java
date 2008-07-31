package org.jboss.seam.test.excel.unit;
        
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WriteException;

import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.jxl.JXLExcelFactory;
import org.jboss.seam.excel.ui.UICell;
import org.jboss.seam.excel.ui.UIFont;
import org.jboss.seam.excel.ui.UICell.CellType;
import org.testng.annotations.Test;

@Test
public class JXLExcelCellFactoryTest
{

   public void getRedFontColor() throws WriteException {
      UICell cell = new UICell();
      UIFont font = new UIFont();
      font.setColor("red");
      cell.getChildren().add(font);
      WritableCellFormat cellFormat = JXLExcelFactory.createCellFormat(cell, null, CellType.text);
      assert cellFormat.getFont().getColour() == Colour.RED;
   }
   
   @Test(expectedExceptions=ExcelWorkbookException.class)
   public void getBadFontColor() throws WriteException {
      UICell cell = new UICell();
      UIFont font = new UIFont();
      font.setColor("poo");
      cell.getChildren().add(font);
      @SuppressWarnings("unused")
      WritableCellFormat cellFormat = JXLExcelFactory.createCellFormat(cell, null, CellType.text);
      assert false;
   }
   
}

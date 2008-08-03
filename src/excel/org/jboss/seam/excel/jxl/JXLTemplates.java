package org.jboss.seam.excel.jxl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.SheetSettings;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import org.jboss.seam.core.Interpolator;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.Template;
import org.jboss.seam.excel.ui.UICell;
import org.jboss.seam.excel.ui.UICellFormat;
import org.jboss.seam.excel.ui.UICellTemplate;
import org.jboss.seam.excel.ui.UIWorksheet;
import org.jboss.seam.excel.ui.UIWorksheetSettings;
import org.jboss.seam.excel.ui.UIWorksheetTemplate;
import org.jboss.seam.excel.ui.UICell.CellType;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * A helper class that is used to cache and merge the cell format templates
 * 
 * @author nik
 * @since 0.2
 */
public class JXLTemplates
{
   private Log log = Logging.getLog(getClass());

   // The separator char for the cells templates-attribute
   private static final String TEMPLATE_SEPARATOR_CHAR = ",";

   // The cache of known cell templates
   private Map<String, UICellTemplate> cellTemplates = new HashMap<String, UICellTemplate>();

   // The cache of known worksheet templates
   private Map<String, UIWorksheetTemplate> worksheetTemplates = new HashMap<String, UIWorksheetTemplate>();

   // A cache for cell types, mapped by UIComponent ID
   private Map<String, CellType> cellDataTypeCache = new HashMap<String, CellType>();

   // A cache for cell formattings, mapped by UIComponent ID
   private Map<String, WritableCellFormat> cellFormatCache = new HashMap<String, WritableCellFormat>();

   // A cache for cell features, mapped by UIComponent ID
   @SuppressWarnings("unused")
   private Map<String, WritableCellFeatures> cellFeaturesCache = new HashMap<String, WritableCellFeatures>();

   /**
    * A class that collects information needed for cell creation
    * 
    * @author Nicklas Karlsson (nickarls@gmail.com)
    */
   protected class CellInfo
   {
      // Cell format of the cell
      private WritableCellFormat cellFormat;

      // Cell features of the cell
      private WritableCellFeatures cellFeatures;

      // Cell contents type of the cell
      private CellType cellType;

      public CellType getCellType()
      {
         return cellType;
      }

      public void setCellType(CellType cellType)
      {
         this.cellType = cellType;
      }

      public WritableCellFormat getCellFormat()
      {
         return cellFormat;
      }

      public WritableCellFeatures getCellFeatures()
      {
         return cellFeatures;
      }

      public void setCellFormat(WritableCellFormat cellFormat)
      {
         this.cellFormat = cellFormat;
      }

      public void setCellFeatures(WritableCellFeatures cellFeatures)
      {
         this.cellFeatures = cellFeatures;
      }
   }

   /**
    * Gets the cell type for a cell. Tries to look it up in a cache based on the
    * component id of the cell. If it's not found, it's created and cached.
    * 
    * @param uiCell The cell to look up
    * @return The data type of a cell
    */
   private CellType getCellDataType(UICell uiCell)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Getting cell data type from cache for #0", uiCell.getId());
      }
      CellType cellDataType = cellDataTypeCache.get(uiCell.getId());
      if (cellDataType == null)
      {
         cellDataType = uiCell.getDataType();
         cellDataTypeCache.put(uiCell.getId(), cellDataType);
      }
      return cellDataType;
   }

   /**
    * Gets a cell format for a cell. Tries to look it up in a cache based on the
    * component id of the cell. If it's not found, it's created and cached.
    * 
    * @param uiCell The cell to format
    * @return The cell format
    */
   private WritableCellFormat getCellFormat(UICell uiCell)
   {
      if (log.isTraceEnabled())
      {
         log.trace("Getting cell format for #0", uiCell.getId());
      }
      WritableCellFormat cellFormat = cellFormatCache.get(uiCell.getId());
      if (cellFormat == null)
      {
         cellFormat = createCellFormat(uiCell);
         cellFormatCache.put(uiCell.getId(), cellFormat);
      }
      return cellFormat;
   }

   /**
    * Gets cell info needed for cell creation
    * 
    * @param uiCell The cell to get info for
    * @return The cell info
    */
   protected CellInfo getCellInfo(UICell uiCell)
   {
      CellInfo cellInfo = new CellInfo();
      cellInfo.setCellFeatures(createCellFeatures(uiCell));
      cellInfo.setCellType(getCellDataType(uiCell));
      cellInfo.setCellFormat(getCellFormat(uiCell));
      return cellInfo;
   }

   /**
    * Adds a template to the stack
    * 
    * @param template The template to add
    */
   protected void addTemplate(Template template)
   {
      switch (template.getType())
      {
      case cell:
         if (cellTemplates.containsKey(template.getName()))
         {
            throw new ExcelWorkbookException(Interpolator.instance().interpolate("The cell template {0} is already registered", template.getName()));
         }
         cellTemplates.put(template.getName(), (UICellTemplate) template);
         break;
      case worksheet:
         if (worksheetTemplates.containsKey(template.getName()))
         {
            throw new ExcelWorkbookException(Interpolator.instance().interpolate("The worksheet template {0} is already registered", template.getName()));
         }
         worksheetTemplates.put(template.getName(), (UIWorksheetTemplate) template);
         break;
      default:
         throw new ExcelWorkbookException(Interpolator.instance().interpolate("Uknown template type {0}", template.getType()));
      }
   }

   /**
    * Merges all cell templates found in the templates attribute of a uiCell to
    * a single list and appends the cell itself to the end of the list (the last
    * cascade step)
    * 
    * @param uiCell The uiCell to check for templates and merge
    * @return The list of merged templates
    */
   private List<UICellFormat> mergeTemplates(UICell uiCell)
   {
      List<UICellFormat> mergeList = new ArrayList<UICellFormat>();
      if (uiCell.getTemplates() != null)
      {
         for (String templateName : uiCell.getTemplates().split(TEMPLATE_SEPARATOR_CHAR))
         {
            UICellTemplate cellTemplate = cellTemplates.get(templateName.trim());
            if (cellTemplate == null)
            {
               String validNames = getValidTemplateNames(cellTemplates.keySet());
               log.warn(Interpolator.instance().interpolate("Could not find cell template {0}, try {1}", templateName, validNames));
            }
            else
            {
               mergeList.add(cellTemplate);
            }
         }
      }
      mergeList.add(uiCell);
      return mergeList;
   }

   /**
    * Returns a list of valid template names in case of error
    * 
    * @param keys The set of key strings to merge
    * @return a comma, separated list of registered names
    */
   private String getValidTemplateNames(Set<String> keys)
   {
      StringBuffer names = new StringBuffer();
      int i = 0;
      for (String name : keys)
      {
         names.append(i++ == 0 ? name : ", " + name);
      }
      return names.toString();
   }

   /**
    * Creates cell features for a list from a list of merged templates
    * 
    * @param uiCell The cell to use as a last step of cascade
    * @return The cell features
    */
   private WritableCellFeatures createCellFeatures(UICell uiCell)
   {
      List<UICellFormat> mergeList = mergeTemplates(uiCell);

      WritableCellFeatures mergedCellFeatures = null;
      for (UICellFormat mergeCellFeature : mergeList)
      {
         mergedCellFeatures = JXLExcelFactory.createCellFeatures(mergeCellFeature, mergedCellFeatures);
      }
      return mergedCellFeatures;
   }

   /**
    * Creates a cell format for a given cell. Puts all requested template to a
    * list and merges them
    * 
    * @param uiCell The cell to format
    * @return A cellformat
    */
   private WritableCellFormat createCellFormat(UICell uiCell)
   {
      List<UICellFormat> mergeList = mergeTemplates(uiCell);

      WritableCellFormat mergedCellFormat = null;
      for (UICellFormat mergeCellFormat : mergeList)
      {
         try
         {
            mergedCellFormat = JXLExcelFactory.createCellFormat(mergeCellFormat, mergedCellFormat, uiCell.getDataType());
         }
         catch (WriteException e)
         {
            throw new ExcelWorkbookException("Could not crete cell format", e);
         }
      }
      return mergedCellFormat;
   }

   /**
    * Applies worksheet settings to the active sheet. First merges templates for
    * settings.
    * 
    * @param worksheet The worksheet to apply the settings to
    * @param uiWorksheet The settings to apply (+ templates)
    */
   protected void applyWorksheetSettings(WritableSheet worksheet, UIWorksheet uiWorksheet)
   {
      List<UIWorksheetSettings> mergeList = new ArrayList<UIWorksheetSettings>();

      if (uiWorksheet.getTemplates() != null)
      {
         for (String templateName : uiWorksheet.getTemplates().split(TEMPLATE_SEPARATOR_CHAR))
         {
            UIWorksheetTemplate worksheetTemplate = worksheetTemplates.get(templateName.trim());
            if (worksheetTemplate == null)
            {
               String validNames = getValidTemplateNames(worksheetTemplates.keySet());
               throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not find worksheet template #0, try [#1]", templateName, validNames));
            }
            mergeList.add(worksheetTemplate);
         }
      }

      mergeList.add(uiWorksheet);

      SheetSettings oldSettings = worksheet.getSettings();
      for (UIWorksheetSettings template : mergeList)
      {
         JXLExcelFactory.applyWorksheetSettings(oldSettings, template);
      }
   }

}

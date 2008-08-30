package org.jboss.seam.excel.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Manager;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.document.DocumentStore;
import org.jboss.seam.excel.ExcelFactory;
import org.jboss.seam.excel.ExcelWorkbook;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.Template;
import org.jboss.seam.excel.ui.ExcelComponent;
import org.jboss.seam.excel.ui.UICell;
import org.jboss.seam.excel.ui.UIColumn;
import org.jboss.seam.excel.ui.UIWorkbook;
import org.jboss.seam.excel.ui.UIWorksheet;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;

/**
 * Excel export class that exports a UIData component to an Excel workbook
 * 
 * @author Nicklas Karlsson (nickarls@gmail.com)
 * @author Daniel Roth (danielc.roth@gmail.com)
 * 
 */
@Name("org.jboss.seam.excel.exporter.excelExporter")
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class ExcelExporter
{
   // The excel workbook implementation
   private ExcelWorkbook excelWorkbook = null;

   private List<Integer> columnWidths = new ArrayList<Integer>();

   /**
    * Helper method to call the exporter and use the default excel workbook implementation
    * @param dataTableId
    */
   public void export(String dataTableId)
   {
      export(dataTableId, "");
   }

   /**
    * Exports the UIData object to Excel workbook. Looks up the component, parse
    * the templates, iterates the columns and the UIOutput elements within
    * 
    * @param dataTableId id of data table to export
    * @param type ExcelWorkbook implementation to use
    */
   @SuppressWarnings("unchecked")
   public void export(String dataTableId, String type)
   {
      // TODO: support "type" ?
      excelWorkbook = ExcelFactory.instance().getExcelWorkbook(type);

      // Gets the datatable
      UIData dataTable = (UIData) FacesContext.getCurrentInstance().getViewRoot().findComponent(dataTableId);
      if (dataTable == null)
      {
         throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not find data table with id #0", dataTableId));
      }

      // Inits the workbook and worksheet
      excelWorkbook.createWorkbook(new UIWorkbook());
      excelWorkbook.createOrSelectWorksheet(new UIWorksheet());

      // Adds templates
      String styleString = StyleParser.getComponentStyle(dataTable);
      Map<String, Map<String, String>> templateMap = StyleParser.getTemplateMap(styleString);
      List<Template> templates = StyleParser.getTemplates(templateMap);
      for (Template template : templates)
      {
         excelWorkbook.addTemplate(template);
      }
      columnWidths = StyleParser.parseColumnWidths(templateMap.get(StyleParser.TEMPLATE_GLOBAL));

      // Saves the datatable var
      String dataTableVar = dataTable.getVar();
      Object oldValue = FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(dataTableVar);

      // Processes the columns
      List<javax.faces.component.UIColumn> columns = ExcelComponent.getChildrenOfType(dataTable.getChildren(), javax.faces.component.UIColumn.class);
      int col = 0;
      for (javax.faces.component.UIColumn column : columns)
      {
         Iterator iterator = UIWorksheet.unwrapIterator(dataTable.getValue());
         processColumn(column, iterator, dataTableVar, col++);
         excelWorkbook.nextColumn();
      }

      // Restores the data table var
      if (oldValue == null)
      {
         FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove(dataTableVar);
      }
      else
      {
         FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(dataTableVar, oldValue);
      }

      // Redirects to the generated document
      redirectExport();

   }

   /**
    * Puts document in store and redirects
    */
   private void redirectExport()
   {
      String viewId = Pages.getViewId(FacesContext.getCurrentInstance());
      String baseName = UIWorkbook.baseNameForViewId(viewId);
      DocumentData documentData = new DocumentData(baseName, excelWorkbook.getDocumentType(), excelWorkbook.getBytes());
      String id = DocumentStore.instance().newId();
      String url = DocumentStore.instance().preferredUrlForContent(baseName, excelWorkbook.getDocumentType().getExtension(), id);
      url = Manager.instance().encodeConversationId(url, viewId);
      DocumentStore.instance().saveData(id, documentData);
      try
      {
         FacesContext.getCurrentInstance().getExternalContext().redirect(url);
      }
      catch (IOException e)
      {
         throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not redirect to #0", url), e);
      }
   }

   /**
    * Processes a datatable column
    * 
    * @param column The column to parse
    * @param iterator The iterator to the data
    * @param var The binding var
    * @param col
    */
   @SuppressWarnings("unchecked")
   private void processColumn(javax.faces.component.UIColumn column, Iterator iterator, String var, int col)
   {
      // Process header facet
      UIComponent headerFacet = column.getFacet(UIColumn.HEADER_FACET_NAME);
      if (headerFacet != null && UIOutput.class.isAssignableFrom(headerFacet.getClass()))
      {
         List<UIOutput> headerOutputs = new ArrayList<UIOutput>();
         headerOutputs.add((UIOutput) headerFacet);
         processOutputs(headerOutputs, "global,header");
      }

      try
      {
         String rendered = ExcelComponent.cmp2String(FacesContext.getCurrentInstance(), column);
         Log log = Logging.getLog(getClass());
         log.warn("Rendered as #0", rendered);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      
      // Process data
      while (iterator.hasNext())
      {
         FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(var, iterator.next());
         List<UIOutput> dataOutputs = ExcelComponent.getChildrenOfType(column.getChildren(), UIOutput.class);
         processOutputs(dataOutputs, "global,data");
      }

      if (columnWidths.size() > col)
      {
         Integer columnWidth = columnWidths.get(col);
         if (columnWidth != null)
         {
            UIColumn uiColumn = new UIColumn(columnWidth);
            excelWorkbook.applyColumnSettings(uiColumn);
         }
      }

   }

   /**
    * Processes all output type elements (in column)
    * 
    * @param outputs The list of outputs to process
    * @param preTemplates The pre-pushed templates
    */
   private void processOutputs(List<UIOutput> outputs, String preTemplates)
   {
      for (UIOutput output : outputs)
      {
         if (!output.isRendered())
         {
            continue;
         }
         UICell cell = new UICell();
         cell.setId(output.getId());
         cell.setValue(output.getValue());

         String cellTemplates = preTemplates;
         String localTemplates = null;
         String outputStyle = StyleParser.getComponentStyle(output);

         Map<String, String> globalTemplate = StyleParser.getTemplateMap(outputStyle).get(StyleParser.TEMPLATE_GLOBAL);
         if (globalTemplate != null)
         {
            localTemplates = globalTemplate.get(StyleParser.LOCAL_TEMPLATE_STYLE);
         }
         if (localTemplates != null)
         {
            cellTemplates = cellTemplates + "," + localTemplates;
         }
         cell.setTemplates(cellTemplates);

         excelWorkbook.addItem(cell);
      }
   }

}

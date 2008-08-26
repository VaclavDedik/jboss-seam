package org.jboss.seam.excel.ui;

import java.io.IOException;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Interpolator;
import org.jboss.seam.excel.ExcelWorkbookException;
import org.jboss.seam.excel.exporter.ExcelExporter;

public class UIExcelExport extends ExcelComponent
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIExcelExport";

   private ExcelExporter exporter = new ExcelExporter();
   private String type;
   private String forDataTable;

   public String getType()
   {
      return (String) valueOf("type", type);
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getForDataTable()
   {
      return (String) valueOf("forDataTable", forDataTable);
   }

   public void setForDataTable(String forDataTable)
   {
      this.forDataTable = forDataTable;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void encodeBegin(javax.faces.context.FacesContext facesContext) throws IOException
   {
      UIData dataTable = (UIData) getParentByClass(getParent(), UIData.class);
      if (dataTable == null)
      {
         if (getForDataTable() == null)
         {
            throw new ExcelWorkbookException("Must define forDataTable attribute if tag is not nested within a datatable");
         }
         dataTable = (HtmlDataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(getForDataTable());
         if (dataTable == null)
         {
            throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not find data table with id #0", getForDataTable()));
         }
      }
      exporter.export(dataTable.getId());
   }

   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

}

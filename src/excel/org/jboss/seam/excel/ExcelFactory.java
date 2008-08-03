package org.jboss.seam.excel;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Interpolator;

@Name("org.jboss.seam.excel.excelFactory")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class ExcelFactory
{

   public final static String DEFAULT_IMPL = "jxl";
   public final static String DEFAULT_NS = "org.jboss.seam.excel";

   private String namespace;

   public static ExcelFactory instance()
   {
      return (ExcelFactory) Component.getInstance(ExcelFactory.class);
   }

   public ExcelWorkbook getExcelWorkbook(String type)
   {

      String namespace = DEFAULT_NS;
      String impl = DEFAULT_IMPL;

      namespace = this.namespace != null ? this.namespace : DEFAULT_NS;
      impl = !"".equals(type) ? type : DEFAULT_IMPL;

      ExcelWorkbook excelWorkbook = (ExcelWorkbook) Component.getInstance(namespace + "." + impl);
      if (excelWorkbook == null)
      {
         throw new ExcelWorkbookException(Interpolator.instance().interpolate("Could not create excel workbook with namespace '#0' and type #1", namespace, type));
      }
      return excelWorkbook;

   }

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

}

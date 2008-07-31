package org.jboss.seam.excel.ui;

import org.jboss.seam.excel.Validation;

public class UIListValidation extends ExcelComponent implements Validation
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.excel.ui.UIListValidation";

   public String getFamily()
   {
      return COMPONENT_TYPE;
   }

   public ValidationType getType()
   {
      return ValidationType.list;
   }
}

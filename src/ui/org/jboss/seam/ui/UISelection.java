package org.jboss.seam.ui;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.jboss.seam.contexts.Contexts;

public class UISelection extends UIParameter
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UISelection";
   
   private String dataModel;
   
   @Override
   public String getName()
   {
      return "dataModelSelection";
   }

   @Override
   public Object getValue()
   {
      Object value = Contexts.lookupInStatefulContexts(dataModel);
      return value==null ? null : 
         dataModel + '[' + ( (DataModel) value ).getRowIndex() + ']';
   }

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      dataModel = (String) values[1];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[2];
      values[0] = super.saveState(context);
      values[1] = dataModel;
      return values;
   }

   public String getDataModel()
   {
      return dataModel;
   }

   public void setDataModel(String dataModel)
   {
      this.dataModel = dataModel;
   }
}

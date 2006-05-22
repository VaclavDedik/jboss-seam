package org.jboss.seam.databinding;

import javax.faces.model.DataModel;

import org.jboss.seam.annotations.datamodel.DataModelSelection;

public class DataModelSelector implements DataSelector<DataModelSelection, DataModel>
{
   
   public String getVariableName(DataModelSelection in)
   {
      return in.value();
   }

   public Object getSelection(DataModel wrapper)
   {
      return wrapper.getRowCount()==0 || wrapper.getRowIndex()<0 ? null : wrapper.getRowData();
   }
   
}

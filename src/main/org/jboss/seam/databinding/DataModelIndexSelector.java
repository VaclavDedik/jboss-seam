package org.jboss.seam.databinding;

import javax.faces.model.DataModel;

import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;

public class DataModelIndexSelector implements DataSelector<DataModelSelectionIndex, DataModel>
{

   public String getVariableName(DataModelSelectionIndex in)
   {
      return in.value();
   }

   public Object getSelection(DataModelSelectionIndex in, DataModel wrapper)
   {
      return wrapper.getRowCount()==0 || wrapper.getRowIndex()<0 ? null : wrapper.getRowIndex();
   }
   
}

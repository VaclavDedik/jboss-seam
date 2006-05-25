package org.jboss.seam.databinding;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.jsf.ListDataModel;

public class DataModelBinder implements DataBinder<DataModel, List, javax.faces.model.DataModel>
{

   public String getVariableName(DataModel out)
   {
      return out.value();
   }

   public ScopeType getVariableScope(DataModel out)
   {
      return out.scope();
   }

   public javax.faces.model.DataModel wrap(List value)
   {
      return new ListDataModel(value);
   }

   public List getWrappedData(javax.faces.model.DataModel wrapper)
   {
      return (List) wrapper.getWrappedData();
   }

   public Object getSelection(javax.faces.model.DataModel wrapper)
   {
      return wrapper.getRowCount()==0 || wrapper.getRowIndex()<0 ? null : wrapper.getRowIndex();
   }

   public boolean isDirty(javax.faces.model.DataModel wrapper, List value)
   {
      return !getWrappedData(wrapper).equals(value);
   }
   
}

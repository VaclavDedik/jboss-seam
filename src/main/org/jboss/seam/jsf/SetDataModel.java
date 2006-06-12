//$Id$
package org.jboss.seam.jsf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

public class SetDataModel extends javax.faces.model.DataModel implements
      Serializable
{
   private int rowIndex = -1;
   private Set set;
   private List entries;

   public SetDataModel()
   {
      super();
   }

   public SetDataModel(Set set)
   {
      if (set == null)
      {
         throw new IllegalArgumentException("null set data");
      }
      setWrappedData(set);
   }

   public int getRowCount()
   {
      if (set==null)
      {
         return -1;
      }
      return set.size();
   }

   public Object getRowData()
   {
      if (set == null)
      {
         return null;
      }
      if ( !isRowAvailable() )
      {
         throw new IllegalArgumentException("row is unavailable");
      }
      return entries.get(rowIndex);
   }

   public int getRowIndex()
   {
      return rowIndex;
   }

   public Object getWrappedData()
   {
      return set;
   }

   public boolean isRowAvailable()
   {
      return entries!=null && rowIndex >= 0 && rowIndex < entries.size();
   }

   public void setRowIndex(int newRowIndex)
   {
      if (newRowIndex < -1)
      {
         throw new IllegalArgumentException("illegal rowIndex " + newRowIndex);
      }
      int oldRowIndex = rowIndex;
      rowIndex = newRowIndex;
      if (set != null && oldRowIndex != newRowIndex)
      {
         Object data = isRowAvailable() ? getRowData() : null;
         DataModelEvent event = new DataModelEvent(this, newRowIndex, data);
         DataModelListener[] listeners = getDataModelListeners();
         for (int i = 0; i < listeners.length; i++)
         {
            listeners[i].rowSelected(event);
         }
      }
   }

   public void setWrappedData(Object data)
   {
      set = (Set) data;
      entries = new ArrayList(set);
      int rowIndex = data != null ? 0 : -1;
      setRowIndex(rowIndex);
   }

   private void writeObject(ObjectOutputStream oos) throws IOException
   {
      oos.writeObject(set);
      oos.writeInt(rowIndex);
      oos.writeObject(entries);
   }

   private void readObject(ObjectInputStream ois) throws IOException,
         ClassNotFoundException
   {
      set = (Set) ois.readObject();
      rowIndex = ois.readInt();
      entries = (List) ois.readObject();
   }

}

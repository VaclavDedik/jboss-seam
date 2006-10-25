//$Id$
package org.jboss.seam.jsf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

public class MapDataModel extends javax.faces.model.DataModel implements
      Serializable
{
   private int rowIndex = -1;
   private Map map;
   private List<Map.Entry> entries;

   public MapDataModel()
   {
      super();
   }

   public MapDataModel(Map map)
   {
      if (map == null)
      {
         throw new IllegalArgumentException("null map data");
      }
      setWrappedData(map);
   }

   @Override
   public int getRowCount()
   {
      if (map == null)
      {
         return -1;
      }
      return map.size();
   }

   /**
    * Returns a Map.Entry
    */
   @Override
   public Object getRowData()
   {
      if (map == null)
      {
         return null;
      }
      if ( !isRowAvailable() )
      {
         throw new IllegalArgumentException("row is unavailable");
      }
      return entries.get(rowIndex);
   }

   @Override
   public int getRowIndex()
   {
      return rowIndex;
   }

   @Override
   public Object getWrappedData()
   {
      return map;
   }

   @Override
   public boolean isRowAvailable()
   {
      return entries!=null && rowIndex >= 0 && rowIndex < entries.size();
   }

   @Override
   public void setRowIndex(int newRowIndex)
   {
      if (newRowIndex < -1)
      {
         throw new IllegalArgumentException("illegal rowIndex " + newRowIndex);
      }
      int oldRowIndex = rowIndex;
      rowIndex = newRowIndex;
      if (map != null && oldRowIndex != newRowIndex)
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

   @Override
   public void setWrappedData(Object data)
   {
      map = (Map) data;
      entries = new ArrayList( map.entrySet() );
      int rowIndex = data != null ? 0 : -1;
      setRowIndex(rowIndex);
   }

   private void writeObject(ObjectOutputStream oos) throws IOException
   {
      oos.writeObject(map);
      oos.writeInt(rowIndex);
      oos.writeObject(entries);
   }

   private void readObject(ObjectInputStream ois) throws IOException,
         ClassNotFoundException
   {
      map = (Map) ois.readObject();
      rowIndex = ois.readInt();
      entries = (List<Map.Entry>) ois.readObject();
   }

}

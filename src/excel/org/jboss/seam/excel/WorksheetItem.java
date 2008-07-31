package org.jboss.seam.excel;

public interface WorksheetItem
{
   
   public enum ItemType {
      cell, image, hyperlink
   }
   
   public abstract ItemType getItemType();
   
}

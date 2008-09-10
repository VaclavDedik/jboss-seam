package org.jboss.seam.excel.css;

import java.util.Map;

public class ColumnStyle
{
   public Boolean autoSize;
   public Boolean hidden;
   public Integer width;
   
   public ColumnStyle(Map<String, Object> styleMap) {
      autoSize = (Boolean) styleMap.get(CSSNames.COLUMN_AUTO_SIZE);
      hidden = (Boolean) styleMap.get(CSSNames.COLUMN_HIDDEN);
      width = (Integer) styleMap.get(CSSNames.COLUMN_WIDTH);
   }
}

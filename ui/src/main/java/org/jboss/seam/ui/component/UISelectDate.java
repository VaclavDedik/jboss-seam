package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

@Deprecated
public abstract class UISelectDate extends UIComponentBase
{

   public abstract String getDateFormat();

   public abstract void setDateFormat(String dateFormat);

   public abstract String getFor();

   public abstract void setFor(String forField);

   public abstract int getStartYear();

   public abstract void setStartYear(int startYear);

   public abstract int getEndYear();

   public abstract void setEndYear(int endYear);
   
   public abstract Integer getFirstDayOfWeek();
   
   public abstract void setFirstDayOfWeek(Integer firstDayOfWeek);

}

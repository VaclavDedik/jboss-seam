package org.jboss.seam.ui.component;

import javax.faces.component.UIComponentBase;

public abstract class UISelectDate extends UIComponentBase
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UISelectDate";

   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.SelectDate";

   public abstract String getDateFormat();

   public abstract void setDateFormat(String dateFormat);

   public abstract String getFor();

   public abstract void setFor(String forField);

   public abstract int getStartYear();

   public abstract void setStartYear(int startYear);

   public abstract int getEndYear();

   public abstract void setEndYear(int endYear);

}
